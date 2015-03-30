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
name|sasl
package|;
end_package

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

begin_comment
comment|/**  * A SASL Mechanism implements this interface in order to provide the  * AmqpAuthenticator with the means of providing authentication services  * in the SASL handshake step.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SaslMechanism
block|{
comment|/**      * Perform the SASL processing for this mechanism type.      *      * @param sasl      *        the SASL server that has read the incoming SASL exchange.      */
name|void
name|processSaslStep
parameter_list|(
name|Sasl
name|sasl
parameter_list|)
function_decl|;
comment|/**      * @return the User Name extracted from the SASL echange or null if none.      */
name|String
name|getUsername
parameter_list|()
function_decl|;
comment|/**      * @return the Password extracted from the SASL echange or null if none.      */
name|String
name|getPassword
parameter_list|()
function_decl|;
comment|/**      * @return the name of the implemented SASL mechanism.      */
name|String
name|getMechanismName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

