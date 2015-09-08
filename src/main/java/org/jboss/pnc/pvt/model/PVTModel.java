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
    
    public void removeProduct(Product product){
    	products.remove(product);
    }
    
    public Product getProductbyId(String id){
    	Product product = new Product();
    	for (Product p : products){
    		if (p.getId().equals(id)){
    			product = p;
    			break;	
    		}	
    	}
    	return product;
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
    
    public void removeRelease(Release release){
    	releases.remove(release);
    }
    
    public Release getReleasebyId(String id){
    	Release release = new Release();
    	for (Release p : releases){
    		if (p.getId().equals(id)){
    			release = p;
    			break;	
    		}	
    	}
    	return release;
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

	public Map<String, Verification> getVerifications() {
		return verifications;
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
