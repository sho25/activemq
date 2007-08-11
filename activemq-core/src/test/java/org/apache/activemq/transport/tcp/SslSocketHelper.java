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
name|io
operator|.
name|IOException
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
name|management
operator|.
name|remote
operator|.
name|JMXPrincipal
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
name|SSLSocket
import|;
end_import

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SslSocketHelper
block|{
specifier|private
name|SslSocketHelper
parameter_list|()
block|{     }
specifier|public
specifier|static
name|SSLSocket
name|createSSLSocket
parameter_list|(
name|String
name|certDistinguishedName
parameter_list|,
name|boolean
name|wantAuth
parameter_list|,
name|boolean
name|needAuth
parameter_list|)
throws|throws
name|IOException
block|{
name|JMXPrincipal
name|principal
init|=
operator|new
name|JMXPrincipal
argument_list|(
name|certDistinguishedName
argument_list|)
decl_stmt|;
name|X509Certificate
name|cert
init|=
operator|new
name|StubX509Certificate
argument_list|(
name|principal
argument_list|)
decl_stmt|;
name|StubSSLSession
name|sslSession
init|=
operator|new
name|StubSSLSession
argument_list|(
name|cert
argument_list|)
decl_stmt|;
name|StubSSLSocket
name|sslSocket
init|=
operator|new
name|StubSSLSocket
argument_list|(
name|sslSession
argument_list|)
decl_stmt|;
name|sslSocket
operator|.
name|setWantClientAuth
argument_list|(
name|wantAuth
argument_list|)
expr_stmt|;
name|sslSocket
operator|.
name|setNeedClientAuth
argument_list|(
name|needAuth
argument_list|)
expr_stmt|;
return|return
name|sslSocket
return|;
block|}
block|}
end_class

end_unit

