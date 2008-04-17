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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|openwire
operator|.
name|OpenWireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_class
specifier|public
class|class
name|SslTransportFactoryTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SslTransportFactoryTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|SslTransportFactory
name|factory
decl_stmt|;
specifier|private
name|boolean
name|verbose
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|=
operator|new
name|SslTransportFactory
argument_list|()
expr_stmt|;
block|}
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
specifier|public
name|void
name|testBindServerOptions
parameter_list|()
throws|throws
name|IOException
block|{
name|SslTransportServer
name|sslTransportServer
init|=
literal|null
decl_stmt|;
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
name|i
operator|&
literal|0x1
operator|)
operator|==
literal|1
decl_stmt|;
specifier|final
name|boolean
name|needClientAuth
init|=
operator|(
name|i
operator|&
literal|0x2
operator|)
operator|==
literal|1
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
try|try
block|{
name|sslTransportServer
operator|=
operator|(
name|SslTransportServer
operator|)
name|factory
operator|.
name|doBind
argument_list|(
operator|new
name|URI
argument_list|(
literal|"ssl://localhost:61616?"
operator|+
name|options
argument_list|)
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
literal|"Unable to bind to address: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Created ServerSocket did not have correct wantClientAuth status."
argument_list|,
name|sslTransportServer
operator|.
name|getWantClientAuth
argument_list|()
argument_list|,
name|wantClientAuth
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Created ServerSocket did not have correct needClientAuth status."
argument_list|,
name|sslTransportServer
operator|.
name|getNeedClientAuth
argument_list|()
argument_list|,
name|needClientAuth
argument_list|)
expr_stmt|;
try|try
block|{
name|sslTransportServer
operator|.
name|stop
argument_list|()
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
literal|"Unable to stop TransportServer: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|int
name|getMthNaryDigit
parameter_list|(
name|int
name|number
parameter_list|,
name|int
name|digitIdx
parameter_list|,
name|int
name|numBase
parameter_list|)
block|{
return|return
operator|(
name|number
operator|/
operator|(
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
name|numBase
argument_list|,
name|digitIdx
argument_list|)
operator|)
operator|)
operator|%
name|numBase
return|;
block|}
specifier|public
name|void
name|testCompositeConfigure
parameter_list|()
throws|throws
name|IOException
block|{
comment|// The 5 options being tested.
name|int
name|optionSettings
index|[]
init|=
operator|new
name|int
index|[
literal|5
index|]
decl_stmt|;
name|String
name|optionNames
index|[]
init|=
block|{
literal|"wantClientAuth"
block|,
literal|"needClientAuth"
block|,
literal|"socket.wantClientAuth"
block|,
literal|"socket.needClientAuth"
block|,
literal|"socket.useClientMode"
block|}
decl_stmt|;
comment|// Using a trinary interpretation of i to set all possible values of
comment|// stub options for socket and transport.
comment|// 2 transport options, 3 socket options, 3 settings for each option =>
comment|// 3^5 = 243 combos.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|243
condition|;
operator|++
name|i
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|5
condition|;
operator|++
name|j
control|)
block|{
comment|// -1 since the option range is [-1,1], not [0,2].
name|optionSettings
index|[
name|j
index|]
operator|=
name|getMthNaryDigit
argument_list|(
name|i
argument_list|,
name|j
argument_list|,
literal|3
argument_list|)
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|optionSettings
index|[
name|j
index|]
operator|!=
operator|-
literal|1
condition|)
block|{
name|options
operator|.
name|put
argument_list|(
name|optionNames
index|[
name|j
index|]
argument_list|,
name|optionSettings
index|[
name|j
index|]
operator|==
literal|1
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
expr_stmt|;
block|}
block|}
name|StubSSLSocket
name|socketStub
init|=
operator|new
name|StubSSLSocket
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|StubSslTransport
name|transport
init|=
literal|null
decl_stmt|;
try|try
block|{
name|transport
operator|=
operator|new
name|StubSslTransport
argument_list|(
literal|null
argument_list|,
name|socketStub
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
literal|"Unable to create StubSslTransport: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|verbose
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Iteration: "
operator|+
name|i
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Map settings: "
operator|+
name|options
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|optionSettings
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"optionSetting["
operator|+
name|x
operator|+
literal|"] = "
operator|+
name|optionSettings
index|[
name|x
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|factory
operator|.
name|compositeConfigure
argument_list|(
name|transport
argument_list|,
operator|new
name|OpenWireFormat
argument_list|()
argument_list|,
name|options
argument_list|)
expr_stmt|;
comment|// lets start the transport to force the introspection
try|try
block|{
name|transport
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore bad connection
block|}
if|if
condition|(
name|socketStub
operator|.
name|getWantClientAuthStatus
argument_list|()
operator|!=
name|optionSettings
index|[
literal|2
index|]
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"sheiite"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"wantClientAuth was not properly set for iteration: "
operator|+
name|i
argument_list|,
name|optionSettings
index|[
literal|0
index|]
argument_list|,
name|transport
operator|.
name|getWantClientAuthStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"needClientAuth was not properly set for iteration: "
operator|+
name|i
argument_list|,
name|optionSettings
index|[
literal|1
index|]
argument_list|,
name|transport
operator|.
name|getNeedClientAuthStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"socket.wantClientAuth was not properly set for iteration: "
operator|+
name|i
argument_list|,
name|optionSettings
index|[
literal|2
index|]
argument_list|,
name|socketStub
operator|.
name|getWantClientAuthStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"socket.needClientAuth was not properly set for iteration: "
operator|+
name|i
argument_list|,
name|optionSettings
index|[
literal|3
index|]
argument_list|,
name|socketStub
operator|.
name|getNeedClientAuthStatus
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"socket.useClientMode was not properly set for iteration: "
operator|+
name|i
argument_list|,
name|optionSettings
index|[
literal|4
index|]
argument_list|,
name|socketStub
operator|.
name|getUseClientModeStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

