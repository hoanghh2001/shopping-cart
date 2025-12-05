package hoang.shop.identity.service;

import hoang.shop.identity.model.User;

public interface CurrentUserService {
    Long getCurrentUserId();

    User getCurrentUser();
}
