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
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_comment
comment|/**  * Base for all broker plugins that wish to provide connection authentication services  */
end_comment

begin_interface
specifier|public
interface|interface
name|AuthenticationBroker
block|{
comment|/**      * Authenticate the given user using the mechanism provided by this service.      *      * @param username      *        the given user name to authenticate, null indicates an anonymous user.      * @param password      *        the given password for the user to authenticate.      * @param peerCertificates      *        for an SSL channel the certificates from remote peer.      *      * @return a new SecurityContext for the authenticated user.      *      * @throws SecurityException if the user cannot be authenticated.      */
name|SecurityContext
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|X509Certificate
index|[]
name|peerCertificates
parameter_list|)
throws|throws
name|SecurityException
function_decl|;
block|}
end_interface

end_unit

