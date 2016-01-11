package org.jboss.pnc.pvt.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 */

@JsonAutoDetect
public class PVTModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Product> products = new ArrayList<>();

    private List<Release> releases = new ArrayList<>();

	private List<VerifyToolType> toolTypes = new ArrayList<>();

	/** 
	 * Tools Map, which contains all Tool instances.
	 * Key of the map is the tool id. 
	 *  
	 */
	private Map<String, VerifyTool> tools = new HashMap<>();

	/**
	 * Verification map, which contains all Verification instances inside PVT.
	 * Key of the map is the Verification id.
	 */
	private Map<String,Verification> verifications = new HashMap<>();

    public void addProduct(Product product) {
        products.add(product);
    }
    
    public void updateProduct(Product product) {
    	
    	for (Product p : products){
    		if (p.getId().equals(product.getId())){
    			int index = products.indexOf(p);
    			products.set(index, product);
    			break;	
    		}	
    	}
    }
    
    public boolean removeProduct(Product product){
		boolean hasRelease = false;
		for(Release release : releases) {
			if(release.getProductId().equals(product.getId())) {
				hasRelease = true;
				break;
			}
		}
		return hasRelease ? false : products.remove(product);

    }
    
    public Product getProductById(String id){
    	for (Product p : products){
    		if (p.getId().equals(id)){
    			return p;	
    		}	
    	}
    	return null;
    }

	/**
	 *
	 * @param releaseId
	 * @return if a release has linked tools, return the linked tools, else return the product default linked tools
	 */
	public List<String> getToolsOfRelease(String releaseId){
		if(releaseId == null) {
			return Collections.emptyList();
		}
		Release release = getReleasebyId(releaseId);
		if(release == null) {
			return Collections.emptyList();
		}

		List<String> releaseTools = release.getTools();
		if(!releaseTools.isEmpty()) {
			return releaseTools;
		}
		else {
			return getProductById(getReleasebyId(releaseId).getProductId()).getTools();
		}
	}



    public List<Product> getProducts() {
        return products;
    }

    public void addRelease(Release release) {
        releases.add(release);
    }
    
    public void updateRelease(Release release) {
    	
    	for (Release r : releases){
    		if (r.getId().equals(release.getId())){
    			int index = releases.indexOf(r);
    			releases.set(index, release);
    			break;	
    		}	
    	}
    }
    
    public boolean removeRelease(Release release){
		if(!release.getTools().isEmpty()) {
			return false;
		}
		else {
			return releases.remove(release);
		}
    }
    
    public Release getReleasebyId(String id){
    	for (Release p : releases){
    		if (p.getId().equals(id)){
    			return p;
    		}	
    	}
    	return null;
    }

	public List<Release> getReleasesByProduct(String productId) {
		List<Release> releases = new ArrayList<>();
		if(productId == null || productId.trim().isEmpty()) {
			return releases;
		}
		for(Release release : this.releases) {
			if(release.getProductId().equals(productId)) {
				releases.add(release);
			}
		}
		Collections.sort(releases, new Comparator<Release>() {
			@Override
			public int compare(Release o1, Release o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return releases;
	}

    public List<Release> getReleases() {
        return releases;
    }

	public List<VerifyToolType> getToolTypes() {
		return toolTypes;
	}

	public Map<String, VerifyTool> getTools() {
		return tools;
	}

	@JsonIgnore
	public List<VerifyTool> getToolsList() {
		return Arrays.asList(tools.values().toArray(new VerifyTool[tools.size()]));
	}

	public VerifyTool getVerifyToolById(String id) {
		return tools.get(id);
	}

	public void addTool(VerifyTool tool) {
		tools.put(tool.getId(), tool);
	}

	public boolean removeTool(VerifyTool tool) {
		boolean success = true;
		for(Verification verification : verifications.values()) {
			if(verification.getToolId().equals(tool.getId())) {
				success = false;
				break;
			}
		}
		return success;
	}

	public Map<String, Verification> getVerifications() {
		return verifications;
	}

	public void updateVerification(Verification verification) {
		if(verification != null) {
			verifications.put(verification.getId(), verification);
		}
	}
	@JsonIgnore
	public List<Verification> getVerificationsList() {
		return Arrays.asList(verifications.values().toArray(new Verification[verifications.size()]));
	}

	public Verification getVerificationById(String id) {
		return verifications.get(id);
	}

	public void addVerification(Verification verification) {
		verifications.put(verification.getId(), verification);
	}

	public boolean removeVerification(Verification verification) {
		boolean success = true;
		for (Release p : releases) {
			if (p.getVerifications().contains(verification.getId())){
				success = false;
				break;
			}
		}
		if(success) {
			verifications.remove(verification.getId());
		}
		return success;
	}

	public List<VerifyTool> getVerifyTools(String... toolsId) {
		if(toolsId == null || toolsId.length == 0) {
			return Collections.emptyList();
		}
		else {
			List<VerifyTool> tools = new ArrayList<>(toolsId.length);
			for(String id : toolsId) {
				tools.add(getVerifyToolById(id));
			}
			return tools;
		}
	}

	public List<VerifyTool> getVerifyTools(List<String> toolsId) {
		if(toolsId == null || toolsId.size() == 0) {
			return Collections.emptyList();
		}
		else {
			List<VerifyTool> tools = new ArrayList<>(toolsId.size());
			for(String id : toolsId) {
				tools.add(getVerifyToolById(id));
			}
			return tools;
		}
	}



	public Release getLastReleaseOfProduct(String productId) {
		Release lastRelease = null;
		for(Release release : releases) {
			if(release.getProductId().equals(productId)) {
				if(lastRelease == null) {
					lastRelease = release;
				}
				else {
					if(release.getCreateTime() > lastRelease.getCreateTime()) {
						lastRelease = release;
					}
				}
			}
		}
		return  lastRelease;
	}


}
