package openstack_examples;

import org.openstack.keystone.KeystoneClient;
import org.openstack.keystone.api.Authenticate;
import org.openstack.keystone.api.ListTenants;
import org.openstack.keystone.model.Access;
import org.openstack.keystone.model.Authentication;
import org.openstack.keystone.model.Authentication.PasswordCredentials;
import org.openstack.keystone.model.Authentication.Token;
import org.openstack.keystone.model.Tenants;
import org.openstack.keystone.utils.KeystoneUtils;
import org.openstack.nova.NovaClient;
import org.openstack.nova.api.ServersCore;
import org.openstack.nova.model.Server;
import org.openstack.nova.model.ServerForCreate;

public class NovaCreateServer {

	private static final String KEYSTONE_AUTH_URL = "http://openstack.infosys.tuwien.ac.at:5000/v2.0";

	private static final String KEYSTONE_USERNAME = "login";

	private static final String KEYSTONE_PASSWORD = "password";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KeystoneClient keystone = new KeystoneClient(KEYSTONE_AUTH_URL);
		Authentication authentication = new Authentication();
		PasswordCredentials passwordCredentials = new PasswordCredentials();
		passwordCredentials.setUsername(KEYSTONE_USERNAME);
		passwordCredentials.setPassword(KEYSTONE_PASSWORD);
		authentication.setPasswordCredentials(passwordCredentials);

		// access with unscoped token
		Access access = keystone.execute(new Authenticate(authentication));

		// use the token in the following requests
		keystone.setToken(access.getToken().getId());

		Tenants tenants = keystone.execute(new ListTenants());

		// try to exchange token using the first tenant
		if (tenants.getList().size() > 0) {

			authentication = new Authentication();
			Token token = new Token();
			token.setId(access.getToken().getId());
			authentication.setToken(token);
			authentication.setTenantId(tenants.getList().get(0).getId());

			access = keystone.execute(new Authenticate(authentication));

			NovaClient novaClient = new NovaClient(
					KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
							"compute", null, "public"), access.getToken()
							.getId());
			
			ServerForCreate serverForCreate = new ServerForCreate();
			serverForCreate.setName("new-server-test");
			// m1.tiny
			serverForCreate.setFlavorRef("href=http://openstack.infosys.tuwien.ac.at:8774/v2/3cc15bd5efe54b9daa5e59e4b3a0bb04/flavors/1");
			// Ubuntu
			serverForCreate.setImageRef("href=http://openstack.infosys.tuwien.ac.at:8774/v2/3cc15bd5efe54b9daa5e59e4b3a0bb04/images/4f62bc16-5fd0-48cc-bc4c-c438d48875bc");
			serverForCreate.setKeyName("aic12");
			serverForCreate.getSecurityGroups().add(
					new ServerForCreate.SecurityGroup("default"));

			Server server = novaClient.execute(ServersCore
					.createServer(serverForCreate));
			System.out.println(server);

		} else {
			System.out.println("No tenants found!");
		}

	}

}
