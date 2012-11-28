package aic.openstack;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.openstack.nova.NovaCommand;
import org.openstack.nova.model.ServerAction.Resume;
import org.openstack.nova.model.ServerAction.Suspend;

public class SuspendResumeServerExtension {
	public class ResumeServer implements NovaCommand<Void> {

		private Resume action;

		private String id;

		public ResumeServer(String id) {
			this.id = id;
			this.action = new Resume();
		}

		@Override
		public Void execute(WebTarget target) {
			target.path("servers").path(id).path("action")
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(action));
			return null;
		}

	}

	public class SuspendServer implements NovaCommand<Void> {

		private Suspend action;

		private String id;

		public SuspendServer(String id) {
			this.id = id;
			this.action = new Suspend();
		}

		@Override
		public Void execute(WebTarget target) {
			target.path("servers").path(id).path("action")
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(action));
			return null;
		}

	}

	public ResumeServer resume(String id) {
		return new ResumeServer(id);
	}

	public SuspendServer suspend(String id) {
		return new SuspendServer(id);
	}
}
