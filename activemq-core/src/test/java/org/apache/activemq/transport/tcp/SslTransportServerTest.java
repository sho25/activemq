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
name|transport
operator|.
name|tcp
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

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
name|net
operator|.
name|URI
import|;
end_import

begin_class
specifier|public
class|class
name|SslTransportServerTest
extends|extends
name|TestCase
block|{
specifier|private
name|SslTransportServer
name|sslTransportServer
init|=
literal|null
decl_stmt|;
specifier|private
name|StubSSLServerSocket
name|sslServerSocket
init|=
literal|null
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|createAndBindTransportServer
parameter_list|(
name|boolean
name|wantClientAuth
parameter_list|,
name|boolean
name|needClientAuth
parameter_list|,
name|String
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|sslServerSocket
operator|=
operator|new
name|StubSSLServerSocket
argument_list|()
expr_stmt|;
name|StubSSLSocketFactory
name|socketFactory
init|=
operator|new
name|StubSSLSocketFactory
argument_list|(
name|sslServerSocket
argument_list|)
decl_stmt|;
try|try
block|{
name|sslTransportServer
operator|=
operator|new
name|SslTransportServer
argument_list|(
literal|null
argument_list|,
operator|new
name|URI
argument_list|(
literal|"ssl://localhost:61616?"
operator|+
name|options
argument_list|)
argument_list|,
name|socketFactory
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Unable to create SslTransportServer."
argument_list|)
expr_stmt|;
block|}
name|sslTransportServer
operator|.
name|setWantClientAuth
argument_list|(
name|wantClientAuth
argument_list|)
expr_stmt|;
name|sslTransportServer
operator|.
name|setNeedClientAuth
argument_list|(
name|needClientAuth
argument_list|)
expr_stmt|;
name|sslTransportServer
operator|.
name|bind
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testWantAndNeedClientAuthSetters
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|boolean
name|wantClientAuth
init|=
operator|(
operator|(
name|i
operator|&
literal|0x1
operator|)
operator|==
literal|1
operator|)
decl_stmt|;
specifier|final
name|boolean
name|needClientAuth
init|=
operator|(
operator|(
name|i
operator|&
literal|0x2
operator|)
operator|==
literal|1
operator|)
decl_stmt|;
specifier|final
name|int
name|expectedWantStatus
init|=
operator|(
name|wantClientAuth
condition|?
name|StubSSLServerSocket
operator|.
name|TRUE
else|:
name|StubSSLServerSocket
operator|.
name|FALSE
operator|)
decl_stmt|;
specifier|final
name|int
name|expectedNeedStatus
init|=
operator|(
name|needClientAuth
condition|?
name|StubSSLServerSocket
operator|.
name|TRUE
else|:
name|StubSSLServerSocket
operator|.
name|FALSE
operator|)
decl_stmt|;
name|createAndBindTransportServer
argument_list|(
name|wantClientAuth
argument_list|,
name|needClientAuth
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Created ServerSocket did not have correct wantClientAuth status."
argument_list|,
name|sslServerSocket
operator|.
name|getWantClientAuthStatus
argument_list|()
argument_list|,
name|expectedWantStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Created ServerSocket did not have correct needClientAuth status."
argument_list|,
name|sslServerSocket
operator|.
name|getNeedClientAuthStatus
argument_list|()
argument_list|,
name|expectedNeedStatus
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testWantAndNeedAuthReflection
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|boolean
name|wantClientAuth
init|=
operator|(
operator|(
name|i
operator|&
literal|0x1
operator|)
operator|==
literal|1
operator|)
decl_stmt|;
specifier|final
name|boolean
name|needClientAuth
init|=
operator|(
operator|(
name|i
operator|&
literal|0x2
operator|)
operator|==
literal|1
operator|)
decl_stmt|;
specifier|final
name|int
name|expectedWantStatus
init|=
operator|(
name|wantClientAuth
condition|?
name|StubSSLServerSocket
operator|.
name|TRUE
else|:
name|StubSSLServerSocket
operator|.
name|FALSE
operator|)
decl_stmt|;
specifier|final
name|int
name|expectedNeedStatus
init|=
operator|(
name|needClientAuth
condition|?
name|StubSSLServerSocket
operator|.
name|TRUE
else|:
name|StubSSLServerSocket
operator|.
name|FALSE
operator|)
decl_stmt|;
name|String
name|options
init|=
literal|"wantClientAuth="
operator|+
operator|(
name|wantClientAuth
condition|?
literal|"true"
else|:
literal|"false"
operator|)
operator|+
literal|"&needClientAuth="
operator|+
operator|(
name|needClientAuth
condition|?
literal|"true"
else|:
literal|"false"
operator|)
decl_stmt|;
name|createAndBindTransportServer
argument_list|(
name|wantClientAuth
argument_list|,
name|needClientAuth
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Created ServerSocket did not have correct wantClientAuth status."
argument_list|,
name|sslServerSocket
operator|.
name|getWantClientAuthStatus
argument_list|()
argument_list|,
name|expectedWantStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Created ServerSocket did not have correct needClientAuth status."
argument_list|,
name|sslServerSocket
operator|.
name|getNeedClientAuthStatus
argument_list|()
argument_list|,
name|expectedNeedStatus
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

