package com.github.entolee.impl;

import java.util.Optional;

interface SignalHandlerInvocationAdapterBuilder {

    Optional<SignalHandlerInvocationAdapter> tryToBuild(final InvocationTarget target);
}
