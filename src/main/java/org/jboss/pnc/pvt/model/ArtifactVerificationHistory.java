/*
 * JBoss, Home of Professional Open Source
 * Copyright @year, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.pnc.pvt.model;

import java.io.Serializable;

/**
 * <code> ArtifactVerificationHistory </code> 
 * 
 * @author <a href="mailto:huwang@redhat.com">Hui Wang</a>
 *
 */
public class ArtifactVerificationHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4942511368011111504L;

	private long id;
    
    private Product procut;
    
    private TestJobMapper testJobMapper;
    
    private int status;
    
    private String output;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the procut
	 */
	public Product getProcut() {
		return procut;
	}

	/**
	 * @param procut the procut to set
	 */
	public void setProcut(Product procut) {
		this.procut = procut;
	}

	/**
	 * @return the testJobMapper
	 */
	public TestJobMapper getTestJobMapper() {
		return testJobMapper;
	}

	/**
	 * @param testJobMapper the testJobMapper to set
	 */
	public void setTestJobMapper(TestJobMapper testJobMapper) {
		this.testJobMapper = testJobMapper;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the output
	 */
	public String getOutput() {
		return output;
	}

	/**
	 * @param output the output to set
	 */
	public void setOutput(String output) {
		this.output = output;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((output == null) ? 0 : output.hashCode());
		result = prime * result + ((procut == null) ? 0 : procut.hashCode());
		result = prime * result + status;
		result = prime * result
				+ ((testJobMapper == null) ? 0 : testJobMapper.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArtifactVerificationHistory other = (ArtifactVerificationHistory) obj;
		if (output == null) {
			if (other.output != null)
				return false;
		} else if (!output.equals(other.output))
			return false;
		if (procut == null) {
			if (other.procut != null)
				return false;
		} else if (!procut.equals(other.procut))
			return false;
		if (status != other.status)
			return false;
		if (testJobMapper == null) {
			if (other.testJobMapper != null)
				return false;
		} else if (!testJobMapper.equals(other.testJobMapper))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ArtifactVerificationHistory [procut=" + procut
				+ ", testJobMapper=" + testJobMapper.toString() + ", status=" + status
				+ ", output=" + output + "]";
	}
    
}
