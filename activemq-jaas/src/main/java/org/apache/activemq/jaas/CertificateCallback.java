begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jaas
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|callback
operator|.
name|Callback
import|;
end_import

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
comment|/**  * A Callback for SSL certificates.  *   * Will return a certificate chain to its client.  *   * @author sepandm@gmail.com (Sepand)  *  */
end_comment

begin_class
specifier|public
class|class
name|CertificateCallback
implements|implements
name|Callback
block|{
name|X509Certificate
name|certificates
index|[]
init|=
literal|null
decl_stmt|;
comment|/**      * Setter for certificate chain.      *       * @param certs The certificates to be returned.      */
specifier|public
name|void
name|setCertificates
parameter_list|(
name|X509Certificate
name|certs
index|[]
parameter_list|)
block|{
name|certificates
operator|=
name|certs
expr_stmt|;
block|}
comment|/**      * Getter for certificate chain.      *       * @return The certificates being carried.      */
specifier|public
name|X509Certificate
index|[]
name|getCertificates
parameter_list|()
block|{
return|return
name|certificates
return|;
block|}
block|}
end_class

end_unit

