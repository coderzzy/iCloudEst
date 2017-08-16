/*
 * Title: CloudSim Toolkit 
 * 标题：Cloudsim工具包
 * 
 * Description: CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds 
 * 描述：Cloudsim建模和仿真云平台工具包
 * 
 * Licence: GPL - http://www.gnu.org/copyleft/gpl.html
 * 
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.lists.PeList;

/**
 * This is a Time-Shared VM Scheduler, which allows over-subscription. In other words, the scheduler
 * still allows the allocation of VMs that require more CPU capacity than is available.
 * Oversubscription results in performance degradation.
 * 这是一个允许过载的分时虚拟机调度程序。
 * 也就是说，在超过可提供的CPU运算能力下，调度程序仍然允许给虚拟机分配。
 * 过载会导致程序性能退化。
 * 
 * @author Anton Beloglazov
 * @author Rodrigo N. Calheiros
 * @since CloudSim Toolkit 3.0
 */
public class VmSchedulerTimeSharedOverSubscription extends VmSchedulerTimeShared {

	/**
	 * Instantiates a new vm scheduler time shared over subscription.
	 * 构造函数
	 * 
	 * @param pelist the list of PEs of the host where the VmScheduler is associated to.
	 */
	public VmSchedulerTimeSharedOverSubscription(List<? extends Pe> pelist) {
		super(pelist);
	}

	/**
	 * Allocates PEs for vm. The policy allows over-subscription. In other words, the policy still
	 * allows the allocation of VMs that require more CPU capacity than is available.
	 * Oversubscription results in performance degradation.
         * It cannot be allocated more CPU capacity for each virtual PE than the MIPS 
         * capacity of a single physical PE.
	 * 为虚拟机分配PEs？。策略允许使用过载。
	 * 也就是说，在超过可提供的CPU运算能力下，调度程序仍然允许给虚拟机分配。
	 * 过载会导致程序性能退化
	     * 相比于MIPS，它不能再被分配更多的CPU运算能力到每一个虚拟的PE上
	     * 一个简单的物理PE的能力
	 * 
	 * @param vmUid the vm uid
	 * @param mipsShareRequested the list of mips share requested
	 * @return true, if successful
	 */
	@Override
	protected boolean allocatePesForVm(String vmUid, List<Double> mipsShareRequested) {
		double totalRequestedMips = 0;

		// if the requested mips is bigger than the capacity of a single PE, we cap
		// the request to the PE's capacity
		// 如果要求的mips比一个单个的PE运算能力大，我们把PE的运算能力覆盖上去，取较小的值
		List<Double> mipsShareRequestedCapped = new ArrayList<Double>();
		double peMips = getPeCapacity();
		for (Double mips : mipsShareRequested) {
			if (mips > peMips) {
				mipsShareRequestedCapped.add(peMips);
				totalRequestedMips += peMips;
			} else {
				mipsShareRequestedCapped.add(mips);
				totalRequestedMips += mips;
			}
		}

		getMipsMapRequested().put(vmUid, mipsShareRequested);
		setPesInUse(getPesInUse() + mipsShareRequested.size());

		if (getVmsMigratingIn().contains(vmUid)) {
			// the destination host only experience 10% of the migrating VM's MIPS
			// 目标主机只经历10%的虚拟机Mips迁移量
			totalRequestedMips *= 0.1;
		}

		if (getAvailableMips() >= totalRequestedMips) {
			List<Double> mipsShareAllocated = new ArrayList<Double>();
			for (Double mipsRequested : mipsShareRequestedCapped) {
				if (getVmsMigratingOut().contains(vmUid)) {
					// performance degradation due to migration = 10% MIPS
					// 因为迁移10%的Mips导致的性能退化
					mipsRequested *= 0.9;
				} else if (getVmsMigratingIn().contains(vmUid)) {
					// the destination host only experience 10% of the migrating VM's MIPS
					// 目标主机只经历10%的虚拟机Mips迁移量
					mipsRequested *= 0.1;
				}
				mipsShareAllocated.add(mipsRequested);
			}

			getMipsMap().put(vmUid, mipsShareAllocated);
			setAvailableMips(getAvailableMips() - totalRequestedMips);
		} else {
			redistributeMipsDueToOverSubscription();
		}

		return true;
	}

	/**
	 * Recalculates distribution of MIPs among VMs, considering eventual shortage of MIPS
	 * compared to the amount requested by VMs.
	 * 在虚拟机之中重新计算MIPs的描述，考虑最终的与虚拟机要求的量相比，mips减少量
	 */
	protected void redistributeMipsDueToOverSubscription() {
		// First, we calculate the scaling factor - the MIPS allocation for all VMs will be scaled
		// proportionally
		// 首先，我们计算比例因子——MIPS给所有虚拟机的分配将按照比例进行分配
		double totalRequiredMipsByAllVms = 0;

		Map<String, List<Double>> mipsMapCapped = new HashMap<String, List<Double>>();
		for (Entry<String, List<Double>> entry : getMipsMapRequested().entrySet()) {

			double requiredMipsByThisVm = 0.0;
			String vmId = entry.getKey();
			List<Double> mipsShareRequested = entry.getValue();
			List<Double> mipsShareRequestedCapped = new ArrayList<Double>();
			double peMips = getPeCapacity();
			for (Double mips : mipsShareRequested) {
				if (mips > peMips) {
					mipsShareRequestedCapped.add(peMips);
					requiredMipsByThisVm += peMips;
				} else {
					mipsShareRequestedCapped.add(mips);
					requiredMipsByThisVm += mips;
				}
			}

			mipsMapCapped.put(vmId, mipsShareRequestedCapped);

			if (getVmsMigratingIn().contains(entry.getKey())) {
				// the destination host only experience 10% of the migrating VM's MIPS
				// 目标主机只有虚拟机MIPS迁移的10%
				requiredMipsByThisVm *= 0.1;
			}
			totalRequiredMipsByAllVms += requiredMipsByThisVm;
		}

		double totalAvailableMips = PeList.getTotalMips(getPeList());
		double scalingFactor = totalAvailableMips / totalRequiredMipsByAllVms;

		// Clear the old MIPS allocation 清除旧的MIPS分配
		getMipsMap().clear();

		// Update the actual MIPS allocated to the VMs 更新分配给虚拟机的实际分配
		for (Entry<String, List<Double>> entry : mipsMapCapped.entrySet()) {
			String vmUid = entry.getKey();
			List<Double> requestedMips = entry.getValue();

			List<Double> updatedMipsAllocation = new ArrayList<Double>();
			for (Double mips : requestedMips) {
				if (getVmsMigratingOut().contains(vmUid)) {
					// the original amount is scaled 初始量按照比例分配
					mips *= scalingFactor;
					// performance degradation due to migration = 10% MIPS 因为10%的迁移导致的性能退化
					mips *= 0.9;
				} else if (getVmsMigratingIn().contains(vmUid)) {
					// the destination host only experiences 10% of the migrating VM's MIPS
					// 目标主机只有虚拟机MIPS迁移的10%
					mips *= 0.1;
					// the final 10% of the requested MIPS are scaled
					// 最终10%的MIPS需求按照比例分配
					mips *= scalingFactor;
				} else {
					mips *= scalingFactor;
				}

				updatedMipsAllocation.add(Math.floor(mips));
			}

			// add in the new map 加入到新的map中
			getMipsMap().put(vmUid, updatedMipsAllocation);

		}

		// As the host is oversubscribed, there no more available MIPS 
		// 因为主机过载，没有多余的可使用的MIPS
		setAvailableMips(0);
	}

}
