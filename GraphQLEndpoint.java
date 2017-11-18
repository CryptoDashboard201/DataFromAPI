package endpoint;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.coxautodev.graphql.tools.SchemaParser;

import authentication.AuthContext;
import errors.SanitizedError;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;
import models.User;
import repositories.UserRepository;
import resolvers.SigninResolver;
import schemas.Mutation;
import schemas.Query;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import models.User;

@WebServlet(urlPatterns="/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {
	public static final long serialVersionUID = 1;
	private static final UserRepository userRepository;
	
	static {
		userRepository = new UserRepository();
	}
	
	@Override
	protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
		User user = request
				.map(req -> req.getHeader("Authorization"))
				.filter(id -> !id.isEmpty())
				.map(id -> id.replace("Bearer ", ""))
				.map(userRepository::findById)
				.orElse(null);
		return new AuthContext(user, request, response);
	}
	
	@Override
	protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
		return errors.stream()
				.filter(e -> e instanceof ExceptionWhileDataFetching || super.isClientError(e))
				.map(e -> e instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) e) : e)
				.collect(Collectors.toList());
	}
	
	public GraphQLEndpoint() {
		super(buildSchema());
	}
	
	private static GraphQLSchema buildSchema() {
		return SchemaParser.newParser()
				.file("schema.graphqls")
				.resolvers(
					new Query(userRepository),
					new Mutation(userRepository),
					new SigninResolver()
				)
				.build()
				.makeExecutableSchema();
	}

	public void getPrice() {
			URL url;
		    HttpURLConnection conn;
		    BufferedReader br;
		    String line;
		    String result = "";
		try {
			while(true) {
				url = new URL("https://min-api.cryptocompare.com/data/pricemultifull?fsyms=BTC&tsyms=USD");
				
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
			
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				result = "";
				while ((line = br.readLine()) != null) {
					result += line;
				}
				System.out.println("result " + result);
				
				JsonParser parser = new JsonParser();
				JsonElement rootNode = parser.parse(result);
				
				if (rootNode.isJsonObject()) {
					JsonObject display = rootNode.getAsJsonObject();
					JsonObject raw = display.getAsJsonObject("RAW");
					JsonObject bitcoin = raw.getAsJsonObject("BTC");
					JsonObject usd = bitcoin.getAsJsonObject("USD");
					JsonElement price = usd.get("PRICE");
					JsonElement low = usd.get("LOW24HOUR");
					JsonElement high = usd.get("HIGH24HOUR");

					// System.out.println("Price :" + price);
					// System.out.println("Low price:" + low);
					// System.out.println("High price:" + high);

					double priceD = Double.parseDouble(price.toString());
					System.out.println("Price in Double" + priceD);
					double priceL = Double.parseDouble(low.toString());
					System.out.println("Lowest Price in Double" + priceL);
					double priceH = Double.parseDouble(high.toString());
					System.out.println("Highest Price in Double" + priceH);
					
					//create an instance of price 
					Price priceClass = new Price(priceD, priceL, priceH);
					
				}
				// while loop with delay function on 5000ms
				Thread.sleep(5000);
				br.close();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	    
	}
}
