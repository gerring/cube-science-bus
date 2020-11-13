package org.jax.cube.event.domain;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Totally fake and short example of bean which
 * is used to submit to an engine to do analysis.
 * 
 * @author gerrim
 *
 */
public class TSubmitBean {

	private String jobName;
	private Path dataPath;
	private int resolution;
	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}
	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	/**
	 * @return the dataPath
	 */
	public Path getDataPath() {
		return dataPath;
	}
	/**
	 * @param dataPath the dataPath to set
	 */
	public void setDataPath(Path dataPath) {
		this.dataPath = dataPath;
	}
	/**
	 * @return the resolution
	 */
	public int getResolution() {
		return resolution;
	}
	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(int resolution) {
		this.resolution = resolution;
	}
	@Override
	public int hashCode() {
		return Objects.hash(dataPath, jobName, resolution);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TSubmitBean))
			return false;
		TSubmitBean other = (TSubmitBean) obj;
		return Objects.equals(dataPath, other.dataPath) && Objects.equals(jobName, other.jobName)
				&& resolution == other.resolution;
	}
}
