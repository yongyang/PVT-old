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
 * <code> TestJobMapper </code> 
 * 
 * @author <a href="mailto:huwang@redhat.com">Hui Wang</a>
 *
 */
public class TestJobMapper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7459370137670337871L;

	private long id;
    
    private Artifact artifact;
    
    private TestTool testTool;

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
	 * @return the artifact
	 */
	public Artifact getArtifact() {
		return artifact;
	}

	/**
	 * @param artifact the artifact to set
	 */
	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	/**
	 * @return the testTool
	 */
	public TestTool getTestTool() {
		return testTool;
	}

	/**
	 * @param testTool the testTool to set
	 */
	public void setTestTool(TestTool testTool) {
		this.testTool = testTool;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((artifact == null) ? 0 : artifact.hashCode());
		result = prime * result
				+ ((testTool == null) ? 0 : testTool.hashCode());
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
		TestJobMapper other = (TestJobMapper) obj;
		if (artifact == null) {
			if (other.artifact != null)
				return false;
		} else if (!artifact.equals(other.artifact))
			return false;
		if (testTool == null) {
			if (other.testTool != null)
				return false;
		} else if (!testTool.equals(other.testTool))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TestJobMapper [artifact=" + artifact.toString() + ", testTool=" + testTool.toString()
				+ "]";
	}

}
