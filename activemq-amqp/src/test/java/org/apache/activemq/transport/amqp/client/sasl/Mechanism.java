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
name|Map
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

begin_comment
comment|/**  * Interface for all SASL authentication mechanism implementations.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Mechanism
extends|extends
name|Comparable
argument_list|<
name|Mechanism
argument_list|>
block|{
comment|/**      * Relative priority values used to arrange the found SASL      * mechanisms in a preferred order where the level of security      * generally defines the preference.      */
specifier|public
enum|enum
name|PRIORITY
block|{
name|LOWEST
argument_list|(
literal|0
argument_list|)
block|,
name|LOW
argument_list|(
literal|1
argument_list|)
block|,
name|MEDIUM
argument_list|(
literal|2
argument_list|)
block|,
name|HIGH
argument_list|(
literal|3
argument_list|)
block|,
name|HIGHEST
argument_list|(
literal|4
argument_list|)
block|;
specifier|private
specifier|final
name|int
name|value
decl_stmt|;
specifier|private
name|PRIORITY
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
empty_stmt|;
comment|/**      * @return return the relative priority of this SASL mechanism.      */
name|int
name|getPriority
parameter_list|()
function_decl|;
comment|/**      * @return the well known name of this SASL mechanism.      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * @return the response buffer used to answer the initial SASL cycle.      * @throws SaslException if an error occurs computing the response.      */
name|byte
index|[]
name|getInitialResponse
parameter_list|()
throws|throws
name|SaslException
function_decl|;
comment|/**      * Create a response based on a given challenge from the remote peer.      *      * @param challenge      *        the challenge that this Mechanism should response to.      *      * @return the response that answers the given challenge.      * @throws SaslException if an error occurs computing the response.      */
name|byte
index|[]
name|getChallengeResponse
parameter_list|(
name|byte
index|[]
name|challenge
parameter_list|)
throws|throws
name|SaslException
function_decl|;
comment|/**      * Sets the user name value for this Mechanism.  The Mechanism can ignore this      * value if it does not utilize user name in it's authentication processing.      *      * @param username      *        The user name given.      */
name|void
name|setUsername
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
comment|/**      * Returns the configured user name value for this Mechanism.      *      * @return the currently set user name value for this Mechanism.      */
name|String
name|getUsername
parameter_list|()
function_decl|;
comment|/**      * Sets the password value for this Mechanism.  The Mechanism can ignore this      * value if it does not utilize a password in it's authentication processing.      *      * @param username      *        The user name given.      */
name|void
name|setPassword
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
comment|/**      * Returns the configured password value for this Mechanism.      *      * @return the currently set password value for this Mechanism.      */
name|String
name|getPassword
parameter_list|()
function_decl|;
comment|/**      * Sets any additional Mechanism specific properties using a Map<String, Object>      *      * @param options      *        the map of additional properties that this Mechanism should utilize.      */
name|void
name|setProperties
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
parameter_list|)
function_decl|;
comment|/**      * The currently set Properties for this Mechanism.      *      * @return the current set of configuration Properties for this Mechanism.      */
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
comment|/**      * Using the configured credentials, check if the mechanism applies or not.      *      * @param username      *      The user name that will be used with this mechanism      * @param password      *      The password that will be used with this mechanism      *      * @return true if the mechanism works with the provided credentials or not.      */
name|boolean
name|isApplicable
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
function_decl|;
comment|/**      * Get the currently configured Authentication ID.      *      * @return the currently set Authentication ID.      */
name|String
name|getAuthzid
parameter_list|()
function_decl|;
comment|/**      * Sets an Authentication ID that some mechanism can use during the      * challenge response phase.      *      * @param authzid      *      The Authentication ID to use.      */
name|void
name|setAuthzid
parameter_list|(
name|String
name|authzid
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

