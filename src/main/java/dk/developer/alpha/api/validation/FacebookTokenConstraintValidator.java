package dk.developer.alpha.api.validation;

import dk.developer.alpha.api.Facebook;
import dk.developer.alpha.api.user.FacebookCredential;
import dk.developer.alpha.api.user.InspectedFacebookToken;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FacebookTokenConstraintValidator implements ConstraintValidator<FacebookToken, FacebookCredential> {
    @Override
    public void initialize(FacebookToken constraintAnnotation) {
    }

    @Override
    public boolean isValid(FacebookCredential value, ConstraintValidatorContext context) {
        Facebook facebook = new Facebook();
        InspectedFacebookToken inspectedFacebookToken = facebook.inspectAccessToken(value.getToken());
        return facebook.validToken(inspectedFacebookToken, value.getUserId());
    }
}
