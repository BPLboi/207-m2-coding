package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private static final String STATUS_CODE = "status";
    private static final String SUCCESS_CODE = "success";
    private static final String SUBBREEDS_CODE = "message";
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException{
        final String API_URL = "https://dog.ceo/api/breed/" + breed + "/list";
        final Request request = new Request.Builder().url(API_URL).build();

        try{
            final Response response = client.newCall(request).execute();
            final JSONObject responseBody = new JSONObject(response.body().string());

            if (responseBody.getString(STATUS_CODE).equals(SUCCESS_CODE)) {
                final JSONArray subbreeds = responseBody.getJSONArray(SUBBREEDS_CODE);
                final List<String> subreeds_list = new ArrayList<>();
                for (int i = 0; i < subbreeds.length(); i++) {
                    subreeds_list.add(subbreeds.getString(i));
                }
                return subreeds_list;
            }
            else {
                throw new BreedNotFoundException(breed);
            }
        }
        catch (IOException | JSONException event){
            throw new BreedNotFoundException(breed);
        }
    }
}