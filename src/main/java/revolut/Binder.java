package revolut;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 *  bind all the services for injection
 */
public class Binder extends AbstractBinder {

    @Override
    protected void configure() {
        bindAsContract(AccountService.class);
    }
}