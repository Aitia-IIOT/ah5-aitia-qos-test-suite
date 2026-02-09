/*******************************************************************************
 *
 * Copyright (c) 2026 AITIA
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 *
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  	AITIA - implementation
 *  	Arrowhead Consortia - conceptualization
 *
 *******************************************************************************/
package ai.aitia.arrowhead.qos.test.jpa.entity;

import java.time.ZonedDateTime;
import java.util.Objects;

import eu.arrowhead.common.Utilities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

@Entity
public class Experiment {

	//=================================================================================================
	// members
	
	@Id
	@Column(nullable = false, unique = true, length = 36)
	private String id;
	
	@Column(nullable = true, length = 1024)
	private String comment;
	
	@Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private ZonedDateTime createdAt;
	
	@Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
	private boolean qosEnabled = false;
	
	@Column(nullable = false)
	private int iteration;
	
	@Column(nullable = false)
	private long wait; // in ms
	
	@Column(nullable = false)
	private int cpuStressPower; // in percent
	
	@Column(nullable = false)
	private long cpuStressLength; // in ms
	
	@Column(nullable = false)
	private long timeoutThreshold; // in percent

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public Experiment() {
	}
	
	//-------------------------------------------------------------------------------------------------
	public Experiment(
			final String id,
			final String comment,
			final boolean qosEnabled,
			final int iteration,
			final long wait,
			final int cpuStressPower,
			final long cpuStressLength,
			final long timeoutThreshold) {
		this.id = id;
		this.comment = comment;
		this.qosEnabled = qosEnabled;
		this.iteration = iteration;
		this.wait = wait;
		this.cpuStressPower = cpuStressPower;
		this.cpuStressLength = cpuStressLength;
		this.timeoutThreshold = timeoutThreshold;
	}

	//-------------------------------------------------------------------------------------------------
	@PrePersist
	public void onCreate() {
		this.createdAt = Utilities.utcNow();
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final Experiment other = (Experiment) obj;
		return id.equals(other.id);
	}
	
	//=================================================================================================
	// boilerplate

	//-------------------------------------------------------------------------------------------------
	public String getId() {
		return id;
	}

	//-------------------------------------------------------------------------------------------------
	public void setId(final String id) {
		this.id = id;
	}

	//-------------------------------------------------------------------------------------------------
	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	//-------------------------------------------------------------------------------------------------
	public void setCreatedAt(final ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	//-------------------------------------------------------------------------------------------------
	public boolean isQosEnabled() {
		return qosEnabled;
	}

	//-------------------------------------------------------------------------------------------------
	public void setQosEnabled(final boolean qosEnabled) {
		this.qosEnabled = qosEnabled;
	}

	//-------------------------------------------------------------------------------------------------
	public int getIteration() {
		return iteration;
	}

	//-------------------------------------------------------------------------------------------------
	public void setIteration(final int iteration) {
		this.iteration = iteration;
	}

	//-------------------------------------------------------------------------------------------------
	public long getWait() {
		return wait;
	}

	//-------------------------------------------------------------------------------------------------
	public void setWait(final long wait) {
		this.wait = wait;
	}

	//-------------------------------------------------------------------------------------------------
	public int getCpuStressPower() {
		return cpuStressPower;
	}

	//-------------------------------------------------------------------------------------------------
	public void setCpuStressPower(final int cpuStressPower) {
		this.cpuStressPower = cpuStressPower;
	}

	//-------------------------------------------------------------------------------------------------
	public long getCpuStressLength() {
		return cpuStressLength;
	}

	//-------------------------------------------------------------------------------------------------
	public void setCpuStressLength(final long cpuStressLength) {
		this.cpuStressLength = cpuStressLength;
	}

	//-------------------------------------------------------------------------------------------------
	public long getTimeoutThreshold() {
		return timeoutThreshold;
	}

	//-------------------------------------------------------------------------------------------------
	public void setTimeoutThreshold(final long timeoutThreshold) {
		this.timeoutThreshold = timeoutThreshold;
	}

	//-------------------------------------------------------------------------------------------------
	public String getComment() {
		return comment;
	}

	//-------------------------------------------------------------------------------------------------
	public void setComment(final String comment) {
		this.comment = comment;
	}
}