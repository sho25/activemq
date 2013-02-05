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
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLServerSocket
import|;
end_import

begin_class
specifier|public
class|class
name|StubSSLServerSocket
extends|extends
name|SSLServerSocket
block|{
specifier|public
specifier|static
specifier|final
name|int
name|UNTOUCHED
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|FALSE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TRUE
init|=
literal|1
decl_stmt|;
specifier|private
name|int
name|wantClientAuthStatus
init|=
name|UNTOUCHED
decl_stmt|;
specifier|private
name|int
name|needClientAuthStatus
init|=
name|UNTOUCHED
decl_stmt|;
specifier|public
name|StubSSLServerSocket
parameter_list|()
throws|throws
name|IOException
block|{              }
specifier|public
name|int
name|getWantClientAuthStatus
parameter_list|()
block|{
return|return
name|wantClientAuthStatus
return|;
block|}
specifier|public
name|int
name|getNeedClientAuthStatus
parameter_list|()
block|{
return|return
name|needClientAuthStatus
return|;
block|}
specifier|public
name|void
name|setWantClientAuth
parameter_list|(
name|boolean
name|want
parameter_list|)
block|{
name|wantClientAuthStatus
operator|=
name|want
condition|?
name|TRUE
else|:
name|FALSE
expr_stmt|;
block|}
specifier|public
name|void
name|setNeedClientAuth
parameter_list|(
name|boolean
name|need
parameter_list|)
block|{
name|needClientAuthStatus
operator|=
name|need
condition|?
name|TRUE
else|:
name|FALSE
expr_stmt|;
block|}
comment|// --- Stubbed methods ---
specifier|public
name|boolean
name|getEnableSessionCreation
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|String
index|[]
name|getEnabledCipherSuites
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
index|[]
name|getEnabledProtocols
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|getNeedClientAuth
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|String
index|[]
name|getSupportedCipherSuites
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|String
index|[]
name|getSupportedProtocols
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|getUseClientMode
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|getWantClientAuth
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|setEnableSessionCreation
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{     }
specifier|public
name|void
name|setEnabledCipherSuites
parameter_list|(
name|String
index|[]
name|suites
parameter_list|)
block|{     }
specifier|public
name|void
name|setEnabledProtocols
parameter_list|(
name|String
index|[]
name|protocols
parameter_list|)
block|{     }
specifier|public
name|void
name|setUseClientMode
parameter_list|(
name|boolean
name|mode
parameter_list|)
block|{     }
block|}
end_class

end_unit
