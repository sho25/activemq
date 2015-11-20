begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|amqp
operator|.
name|client
operator|.
name|sasl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSSecurityException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|sasl
operator|.
name|SaslException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|qpid
operator|.
name|proton
operator|.
name|engine
operator|.
name|Sasl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Manage the SASL authentication process  */
end_comment

begin_class
specifier|public
class|class
name|SaslAuthenticator
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SaslAuthenticator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Sasl
name|sasl
decl_stmt|;
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
specifier|private
specifier|final
name|String
name|authzid
decl_stmt|;
specifier|private
name|Mechanism
name|mechanism
decl_stmt|;
specifier|private
name|String
name|mechanismRestriction
decl_stmt|;
comment|/**      * Create the authenticator and initialize it.      *      * @param sasl      *        The Proton SASL entry point this class will use to manage the authentication.      * @param username      *        The user name that will be used to authenticate.      * @param password      *        The password that will be used to authenticate.      * @param authzid      *        The authzid used when authenticating (currently only with PLAIN)      * @param mechanismRestriction      *        A particular mechanism to use (if offered by the server) or null to allow selection.      */
specifier|public
name|SaslAuthenticator
parameter_list|(
name|Sasl
name|sasl
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|authzid
parameter_list|,
name|String
name|mechanismRestriction
parameter_list|)
block|{
name|this
operator|.
name|sasl
operator|=
name|sasl
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|authzid
operator|=
name|authzid
expr_stmt|;
name|this
operator|.
name|mechanismRestriction
operator|=
name|mechanismRestriction
expr_stmt|;
block|}
comment|/**      * Process the SASL authentication cycle until such time as an outcome is determine. This      * method must be called by the managing entity until the return value is true indicating a      * successful authentication or a JMSSecurityException is thrown indicating that the      * handshake failed.      *      * @throws JMSSecurityException      */
specifier|public
name|boolean
name|authenticate
parameter_list|()
throws|throws
name|SecurityException
block|{
switch|switch
condition|(
name|sasl
operator|.
name|getState
argument_list|()
condition|)
block|{
case|case
name|PN_SASL_IDLE
case|:
name|handleSaslInit
argument_list|()
expr_stmt|;
break|break;
case|case
name|PN_SASL_STEP
case|:
name|handleSaslStep
argument_list|()
expr_stmt|;
break|break;
case|case
name|PN_SASL_FAIL
case|:
name|handleSaslFail
argument_list|()
expr_stmt|;
break|break;
case|case
name|PN_SASL_PASS
case|:
return|return
literal|true
return|;
default|default:
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|handleSaslInit
parameter_list|()
throws|throws
name|SecurityException
block|{
try|try
block|{
name|String
index|[]
name|remoteMechanisms
init|=
name|sasl
operator|.
name|getRemoteMechanisms
argument_list|()
decl_stmt|;
if|if
condition|(
name|remoteMechanisms
operator|!=
literal|null
operator|&&
name|remoteMechanisms
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|mechanism
operator|=
name|findMatchingMechanism
argument_list|(
name|remoteMechanisms
argument_list|)
expr_stmt|;
if|if
condition|(
name|mechanism
operator|!=
literal|null
condition|)
block|{
name|mechanism
operator|.
name|setUsername
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|mechanism
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|mechanism
operator|.
name|setAuthzid
argument_list|(
name|authzid
argument_list|)
expr_stmt|;
comment|// TODO - set additional options from URI.
comment|// TODO - set a host value.
name|sasl
operator|.
name|setMechanisms
argument_list|(
name|mechanism
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|response
init|=
name|mechanism
operator|.
name|getInitialResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
operator|&&
name|response
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|sasl
operator|.
name|send
argument_list|(
name|response
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// TODO - Better error message.
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"Could not find a matching SASL mechanism for the remote peer."
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SaslException
name|se
parameter_list|)
block|{
comment|// TODO - Better error message.
name|SecurityException
name|jmsse
init|=
operator|new
name|SecurityException
argument_list|(
literal|"Exception while processing SASL init."
argument_list|)
decl_stmt|;
name|jmsse
operator|.
name|initCause
argument_list|(
name|se
argument_list|)
expr_stmt|;
throw|throw
name|jmsse
throw|;
block|}
block|}
specifier|private
name|Mechanism
name|findMatchingMechanism
parameter_list|(
name|String
modifier|...
name|remoteMechanisms
parameter_list|)
block|{
name|Mechanism
name|match
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Mechanism
argument_list|>
name|found
init|=
operator|new
name|ArrayList
argument_list|<
name|Mechanism
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|remoteMechanism
range|:
name|remoteMechanisms
control|)
block|{
if|if
condition|(
name|mechanismRestriction
operator|!=
literal|null
operator|&&
operator|!
name|mechanismRestriction
operator|.
name|equals
argument_list|(
name|remoteMechanism
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Skipping {} mechanism because it is not the configured mechanism restriction {}"
argument_list|,
name|remoteMechanism
argument_list|,
name|mechanismRestriction
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|remoteMechanism
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"PLAIN"
argument_list|)
condition|)
block|{
name|found
operator|.
name|add
argument_list|(
operator|new
name|PlainMechanism
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remoteMechanism
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ANONYMOUS"
argument_list|)
condition|)
block|{
name|found
operator|.
name|add
argument_list|(
operator|new
name|AnonymousMechanism
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remoteMechanism
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"CRAM-MD5"
argument_list|)
condition|)
block|{
name|found
operator|.
name|add
argument_list|(
operator|new
name|CramMD5Mechanism
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unknown remote mechanism {}, skipping"
argument_list|,
name|remoteMechanism
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Sorts by priority using Mechanism comparison and return the last value in
comment|// list which is the Mechanism deemed to be the highest priority match.
name|Collections
operator|.
name|sort
argument_list|(
name|found
argument_list|)
expr_stmt|;
name|match
operator|=
name|found
operator|.
name|get
argument_list|(
name|found
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Best match for SASL auth was: {}"
argument_list|,
name|match
argument_list|)
expr_stmt|;
return|return
name|match
return|;
block|}
specifier|private
name|void
name|handleSaslStep
parameter_list|()
throws|throws
name|SecurityException
block|{
try|try
block|{
if|if
condition|(
name|sasl
operator|.
name|pending
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|byte
index|[]
name|challenge
init|=
operator|new
name|byte
index|[
name|sasl
operator|.
name|pending
argument_list|()
index|]
decl_stmt|;
name|sasl
operator|.
name|recv
argument_list|(
name|challenge
argument_list|,
literal|0
argument_list|,
name|challenge
operator|.
name|length
argument_list|)
expr_stmt|;
name|byte
index|[]
name|response
init|=
name|mechanism
operator|.
name|getChallengeResponse
argument_list|(
name|challenge
argument_list|)
decl_stmt|;
name|sasl
operator|.
name|send
argument_list|(
name|response
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SaslException
name|se
parameter_list|)
block|{
comment|// TODO - Better error message.
name|SecurityException
name|jmsse
init|=
operator|new
name|SecurityException
argument_list|(
literal|"Exception while processing SASL step."
argument_list|)
decl_stmt|;
name|jmsse
operator|.
name|initCause
argument_list|(
name|se
argument_list|)
expr_stmt|;
throw|throw
name|jmsse
throw|;
block|}
block|}
specifier|private
name|void
name|handleSaslFail
parameter_list|()
throws|throws
name|SecurityException
block|{
comment|// TODO - Better error message.
throw|throw
operator|new
name|SecurityException
argument_list|(
literal|"Client failed to authenticate"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

