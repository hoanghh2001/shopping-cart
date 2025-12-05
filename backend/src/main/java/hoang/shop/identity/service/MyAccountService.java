package hoang.shop.identity.service;

import hoang.shop.identity.dto.request.ChangeEmailRequest;
import hoang.shop.identity.dto.request.ChangePasswordRequest;
import hoang.shop.identity.dto.request.DeleteAccountRequest;
import hoang.shop.identity.dto.request.VerifyPasswordRequest;
import hoang.shop.identity.dto.response.MyAccountResponse;
import hoang.shop.identity.dto.response.SessionInfoResponse;

import java.util.List;

public interface MyAccountService {
    MyAccountResponse getMyAccount();

    void changePassword(ChangePasswordRequest request);

    void changeEmail(ChangeEmailRequest request);

    void deleteAccount(DeleteAccountRequest request);

    List<SessionInfoResponse> getSessions();

    void revokeSession(Long sessionId);

    void revokeAllSessions();


}
