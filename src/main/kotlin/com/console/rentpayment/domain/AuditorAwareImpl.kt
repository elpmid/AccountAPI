package com.console.rentpayment.domain

import org.springframework.data.domain.AuditorAware
import java.util.Optional

open class AuditorAwareImpl : AuditorAware<String> {

    @Override
    override fun getCurrentAuditor(): Optional<String> {
        //TODO hook up with Spring Security
        return Optional.of("System")
    }
}