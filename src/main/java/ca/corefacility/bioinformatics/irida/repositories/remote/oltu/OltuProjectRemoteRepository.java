package ca.corefacility.bioinformatics.irida.repositories.remote.oltu;

import org.springframework.core.ParameterizedTypeReference;

import ca.corefacility.bioinformatics.irida.repositories.remote.ProjectRemoteRepository;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.RemoteProject;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ListResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.model.resource.ResourceWrapper;
import ca.corefacility.bioinformatics.irida.repositories.remote.resttemplate.OAuthTokenRestTemplate;

/**
 * Remote repository for retrieving {@link RemoteProject}s
 * @author Thomas Matthews <thomas.matthews@phac-aspc.gc.ca>
 *
 */
public class OltuProjectRemoteRepository extends OltuGenericRepository<RemoteProject> implements ProjectRemoteRepository{

	public final static String relativeURI = "projects";
	
	//the type references for this repo
	private static ParameterizedTypeReference<ListResourceWrapper<RemoteProject>> listTypeReference = new ParameterizedTypeReference<ListResourceWrapper<RemoteProject>>() {};
	private static ParameterizedTypeReference<ResourceWrapper<RemoteProject>> objectTypeReference = new ParameterizedTypeReference<ResourceWrapper<RemoteProject>>() {};
	
	/**
	 * Create a new {@link OltuProjectRemoteRepository} with the given rest template
	 * @param restTemplate a {@link OAuthTokenRestTemplate}
	 */
	public OltuProjectRemoteRepository(OAuthTokenRestTemplate restTemplate){
		super(restTemplate,relativeURI,listTypeReference,objectTypeReference);
	}
}
