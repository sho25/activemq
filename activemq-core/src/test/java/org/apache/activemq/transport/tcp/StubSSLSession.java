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
name|tcp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|Certificate
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

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLPeerUnverifiedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSessionContext
import|;
end_import

begin_class
class|class
name|StubSSLSession
implements|implements
name|SSLSession
block|{
name|X509Certificate
name|cert
decl_stmt|;
name|boolean
name|isVerified
decl_stmt|;
specifier|public
name|StubSSLSession
parameter_list|(
name|X509Certificate
name|cert
parameter_list|)
block|{
if|if
condition|(
name|cert
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|isVerified
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|cert
operator|=
name|cert
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|isVerified
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|cert
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setIsVerified
parameter_list|(
name|boolean
name|verified
parameter_list|)
block|{
name|this
operator|.
name|isVerified
operator|=
name|verified
expr_stmt|;
block|}
specifier|public
name|Certificate
index|[]
name|getPeerCertificates
parameter_list|()
throws|throws
name|SSLPeerUnverifiedException
block|{
if|if
condition|(
name|this
operator|.
name|isVerified
condition|)
return|return
operator|new
name|X509Certificate
index|[]
block|{
name|this
operator|.
name|cert
block|}
return|;
else|else
throw|throw
operator|new
name|SSLPeerUnverifiedException
argument_list|(
literal|"Socket is unverified."
argument_list|)
throw|;
block|}
comment|// --- Stubbed methods ---
specifier|public
name|byte
index|[]
name|getId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|SSLSessionContext
name|getSessionContext
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|long
name|getLastAccessedTime
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|void
name|invalidate
parameter_list|()
block|{     }
specifier|public
name|boolean
name|isValid
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|putValue
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{     }
specifier|public
name|Object
name|getValue
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|removeValue
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{     }
specifier|public
name|String
index|[]
name|getValueNames
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Certificate
index|[]
name|getLocalCertificates
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|javax
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
index|[]
name|getPeerCertificateChain
parameter_list|()
throws|throws
name|SSLPeerUnverifiedException
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Principal
name|getPeerPrincipal
parameter_list|()
throws|throws
name|SSLPeerUnverifiedException
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Principal
name|getLocalPrincipal
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getCipherSuite
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getProtocol
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
name|getPeerHost
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getPeerPort
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getPacketBufferSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getApplicationBufferSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

