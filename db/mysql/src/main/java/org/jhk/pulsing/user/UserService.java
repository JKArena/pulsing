package org.jhk.pulsing.user;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.jhk.pulsing.serialization.avro.records.User;
import org.jhk.pulsing.serialization.avro.records.UserId;
import org.jhk.pulsing.shared.util.RedisConstants.INVITATION_ID;
import org.jhk.pulsing.client.payload.Result;
import static org.jhk.pulsing.client.payload.Result.CODE.*;

import org.jhk.pulsing.db.mysql.user.MySqlUserDao;
import org.jhk.pulsing.client.payload.light.UserLight;
import org.jhk.pulsing.user.IUserService;
import org.springframework.stereotype.Service;

/**
 * Note that if transactional bean is implementing an interface, by default the proxy will be a Java Dynamic Proxy. 
 * This means that only external method calls that come in through the proxy will be intercepted any self-invocation calls 
 * will not start any transaction even if the method is annotated with @Transactional.
 * 
 * Also rollback only occur during RuntimeException so paradigm is to throw a RuntimeException when you wish for a 
 * rollback
 * 
 * @author Ji Kim
 */
@Service
public class UserService implements IUserService {
    
    @Resource(name="mySqlUserDao")
    private MySqlUserDao mySqlUserDao;
    
    @Override
    public Result<User> getUser(UserId userId) {
        Result<User> result = new Result<>(FAILURE, null, "Unable to find " + userId);
        
        Optional<User> user = mySqlUserDao.getUser(userId);
        if(user.isPresent()) {
            result = new Result<>(SUCCESS, user.get());
        }
        
        return result;
    }

    @Override
    public Result<User> createUser(User user) {
        
        if(mySqlUserDao.isEmailTaken(user.getEmail().toString())) {
            return new Result<User>(FAILURE, null, "Email is already taken " + user.getEmail());
        }
        
        Result<User> cUser = new Result<User>(FAILURE, null, "Failed in creating " + user);
        
        try {
            cUser = mySqlUserDao.createUser(user);
        } catch(RuntimeException eException) {
        }
        
        return cUser;
    }

    @Override
    public Result<User> validateUser(String email, String password) {
        Optional<User> user = mySqlUserDao.validateUser(email, password);
        
        return user.isPresent() ? new Result<>(SUCCESS, user.get()) : new Result<>(FAILURE, null, "Failed in validating " + email + " : " + password);
    }
    
    @Override
    public Result<String> logout(UserId userId) {
        
        return new Result<>(SUCCESS, "loggedOut");
    }
    
}
