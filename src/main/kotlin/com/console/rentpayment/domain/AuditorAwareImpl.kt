package com.example.domain

import org.springframework.data.domain.AuditorAware

/**
 * Created by Nick on 9/11/2016.
 */

open class AuditorAwareImpl : AuditorAware<String> {

    @Override
    override fun getCurrentAuditor(): String {
        //TODO hook up with Spring Security
        return "System"
    }
}