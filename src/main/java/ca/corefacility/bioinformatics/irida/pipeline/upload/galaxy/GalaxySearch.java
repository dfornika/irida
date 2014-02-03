package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy;

import static com.google.common.base.Preconditions.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyFolderPath;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyObjectName;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.RolesClient;
import com.github.jmchilton.blend4j.galaxy.UsersClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.github.jmchilton.blend4j.galaxy.beans.Role;
import com.github.jmchilton.blend4j.galaxy.beans.User;

/**
 * Class containing methods used to search for information within a Galaxy instance.
 * 
 * @author aaron
 *
 */
public class GalaxySearch
{	
	private GalaxyInstance galaxyInstance;
	
	/**
	 * Builds a new Galaxy search object around the passed GalaxyInstance.
	 * @param galaxyInstance  The GalaxyInstance object used to connect to Galaxy.
	 */
	public GalaxySearch(GalaxyInstance galaxyInstance)
	{
		checkNotNull(galaxyInstance, "galaxyInstance is null");
		
		this.galaxyInstance = galaxyInstance;
	}
	
	/**
	 * Given an email, finds a corresponding users private Role object in Galaxy with that email.
	 * @param email  The email of the user to search.
	 * @return  A private Role object of the user with the corresponding email.
	 * @throws GalaxyUserNoRoleException If no role can be found for the user.
	 */
	public Role findUserRoleWithEmail(GalaxyAccountEmail email) throws GalaxyUserNoRoleException
	{
		checkNotNull(email, "email is null");
		
		Role role = null;
		
		RolesClient rolesClient = galaxyInstance.getRolesClient();
		if (rolesClient != null)
		{
			for (Role curr : rolesClient.getRoles())
			{
				if (email.getName().equals(curr.getName()))
				{
					role = curr;
					break;
				}
			}
		}
		
		if (role == null)
		{
			throw new GalaxyUserNoRoleException("No role exists for user " + email);
		}
		else
		{
			return role;
		}
	}
	
	/**
	 * Given an email, finds a corresponding User object in Galaxy with that email.
	 * @param email  The email of the user to search.
	 * @return  A User object of the user with the corresponding email.
	 * @throws GalaxyUserNotFoundException If no such user exists.
	 */
	public User findUserWithEmail(GalaxyAccountEmail email) throws GalaxyUserNotFoundException
	{
		checkNotNull(email, "email is null");
		
		User user = null;
		
		UsersClient usersClient = galaxyInstance.getUsersClient();
		if (usersClient != null)
		{
			for (User curr : usersClient.getUsers())
			{
				if (email.getName().equals(curr.getEmail()))
				{
					user = curr;
					break;
				}
			}
		}
		
		if (user == null)
		{
			throw new GalaxyUserNotFoundException("Galaxy user " + email + " not found");
		}
		else
		{
			return user;
		}
	}
	
	/**
	 * Given a library ID, searches for the corresponding Library object.
	 * @param libraryId  The libraryId to search for.
	 * @return  A Library object for this Galaxy library.
	 * @throws NoLibraryFoundException If no library is found with the passed id.
	 */
	public Library findLibraryWithId(String libraryId) throws NoLibraryFoundException
	{
		checkNotNull(libraryId, "libraryId is null");
		
		Library library = null;
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<Library> libraries = librariesClient.getLibraries();
		for (Library curr : libraries)
		{
			if (libraryId.equals(curr.getId()))
			{
				library = curr;
			}
		}
		
		if (library == null)
		{
			throw new NoLibraryFoundException("No library found with id " + libraryId);
		}
		else
		{
			return library;
		}
	}
	
	/**
	 * Gets a Map listing all contents of the passed Galaxy library to the LibraryContent object.
	 * @param libraryId  The library to get all contents from.
	 * @return  A Map mapping the path of the library content to the LibraryContent object.
	 * @throws NoLibraryFoundException If no library is found with the given id.
	 */
	public Map<String,LibraryContent> libraryContentAsMap(String libraryId) throws NoLibraryFoundException
	{
		checkNotNull(libraryId, "libraryId is null");
		
		Map<String,LibraryContent> map = null;
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<LibraryContent> libraryContents = librariesClient.getLibraryContents(libraryId);
		
		if (libraryContents != null)
		{	
			map = new HashMap<String,LibraryContent>();
			
			for (LibraryContent content : libraryContents)
			{
				map.put(content.getName(), content);
			}
		}
		
		if (libraryContents == null)
		{
			throw new NoLibraryFoundException("No library found with id " + libraryId);
		}
		else
		{
			return map;
		}
	}
	
	/**
	 * Given a library name, searches for a list of matching Library objects.
	 * @param libraryName  The name of the library to search for.
	 * @return  A list of Library objects matching the given name.
	 * @throws NoLibraryFoundException If no libraries are found with the passed name.
	 */
	public List<Library> findLibraryWithName(GalaxyObjectName libraryName) throws NoLibraryFoundException
	{		
		checkNotNull(libraryName, "libraryName is null");
		
		List<Library> libraries = new LinkedList<Library>();
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<Library> allLibraries = librariesClient.getLibraries();
		
		if (allLibraries != null)
		{
    		for (Library curr : allLibraries)
    		{
    			if (libraryName.getName().equals(curr.getName()))
    			{
    				libraries.add(curr);
    			}
    		}
		}
		
		if (libraries.isEmpty())
		{
			throw new NoLibraryFoundException("No library found with name " + libraryName);
		}
		else
		{
			return libraries;
		}
	}
	
	/**
	 * Given a libraryId and a folder name, search for the corresponding LibraryContent object within this library.
	 * @param libraryId  The ID of the library to search for.
	 * @param folderName  The name of the folder to search for (only finds first instance of this folder name).
	 * @return  A LibraryContent within the given library with the given name, or null if no such folder exists.
	 * @throws NoGalaxyContentFoundException If no library content could be found for the given parameters.
	 */
	public LibraryContent findLibraryContentWithId(String libraryId, @Valid GalaxyFolderPath folderPath)
			throws NoGalaxyContentFoundException
	{
		checkNotNull(libraryId, "libraryId is null");
		checkNotNull(folderPath, "folderPath is null");
		
		LibraryContent folder = null;
		
		LibrariesClient librariesClient = galaxyInstance.getLibrariesClient();
		List<LibraryContent> libraryContents = librariesClient.getLibraryContents(libraryId);
		
		if (libraryContents != null)
		{
			for (LibraryContent content : libraryContents)
			{
				if ("folder".equals(content.getType()))
				{
    				if (folderPath.getName().equals(content.getName()))
    				{
    					folder = content;
    					break;
    				}
				}
			}
		}
		
		if (folder == null)
		{
			throw new NoGalaxyContentFoundException("No library content found for libraryId=" + libraryId
					+ ", folderPath=" + folderPath);
		}
		else
		{
			return folder;
		}
	}

	/**
	 * Determines if the passed Galaxy user exists within the Galaxy instance.
	 * @param galaxyUserEmail  The user email address to check.
	 * @return  True if this user exists, false otherwise.
	 */
	public boolean galaxyUserExists(GalaxyAccountEmail galaxyUserEmail)
    {
		User user = null;
		
		try
        {
	        user = findUserWithEmail(galaxyUserEmail);
	        return user != null;
        }
		catch (GalaxyUserNotFoundException e)
        {
	        return false;
        }
    }
}
