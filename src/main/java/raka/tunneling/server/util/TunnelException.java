package raka.tunneling.server.util;

import javax.management.remote.SubjectDelegationPermission;

public class TunnelException extends RuntimeException{

	public TunnelException(String message) {
		super(message);
	}
	public TunnelException(Exception ex) {
		super(ex.toString(),ex);
	}
}
