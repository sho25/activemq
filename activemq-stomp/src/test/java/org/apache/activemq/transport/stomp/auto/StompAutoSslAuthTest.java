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
name|stomp
operator|.
name|auto
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|Socket
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|SSLSocketFactory
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
name|broker
operator|.
name|Broker
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
name|broker
operator|.
name|BrokerFilter
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
name|broker
operator|.
name|BrokerPlugin
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
name|broker
operator|.
name|ConnectionContext
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
name|command
operator|.
name|ConnectionInfo
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
name|transport
operator|.
name|stomp
operator|.
name|Stomp
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
name|transport
operator|.
name|stomp
operator|.
name|StompTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|StompAutoSslAuthTest
extends|extends
name|StompTestSupport
block|{
specifier|private
specifier|final
name|boolean
name|isNio
decl_stmt|;
specifier|private
name|boolean
name|hasCertificate
init|=
literal|false
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"isNio={0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|false
block|}
block|,
block|{
literal|true
block|}
block|}
argument_list|)
return|;
block|}
comment|/**      * @param isNio      */
specifier|public
name|StompAutoSslAuthTest
parameter_list|(
name|boolean
name|isNio
parameter_list|)
block|{
name|this
operator|.
name|isNio
operator|=
name|isNio
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseAutoSslConnector
parameter_list|()
block|{
return|return
operator|!
name|isNio
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUseAutoNioPlusSslConnector
parameter_list|()
block|{
return|return
name|isNio
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|hasCertificate
operator|=
literal|false
expr_stmt|;
name|stompConnect
argument_list|()
expr_stmt|;
name|connection
operator|=
name|cf
operator|.
name|createConnection
argument_list|(
literal|"system"
argument_list|,
literal|"manager"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Socket
name|createSocket
parameter_list|()
throws|throws
name|IOException
block|{
name|SocketFactory
name|factory
init|=
name|SSLSocketFactory
operator|.
name|getDefault
argument_list|()
decl_stmt|;
return|return
name|factory
operator|.
name|createSocket
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|isNio
condition|?
name|this
operator|.
name|autoNioSslPort
else|:
name|this
operator|.
name|autoSslPort
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getAdditionalConfig
parameter_list|()
block|{
return|return
literal|"?transport.needClientAuth=true&transport.verifyHostName=false"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addAdditionalPlugins
parameter_list|(
name|List
argument_list|<
name|BrokerPlugin
argument_list|>
name|plugins
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|addAdditionalPlugins
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
name|plugins
operator|.
name|add
argument_list|(
operator|new
name|BrokerPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Broker
name|installPlugin
parameter_list|(
name|Broker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|BrokerFilter
argument_list|(
name|broker
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|//The second time should contain the certificate
name|hasCertificate
operator|=
name|info
operator|.
name|getTransportContext
argument_list|()
operator|instanceof
name|X509Certificate
index|[]
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testConnect
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|connectFrame
init|=
literal|"CONNECT\n"
operator|+
literal|"login:system\n"
operator|+
literal|"passcode:manager\n"
operator|+
literal|"request-id:1\n"
operator|+
literal|"\n"
operator|+
name|Stomp
operator|.
name|NULL
decl_stmt|;
name|stompConnection
operator|.
name|sendFrame
argument_list|(
name|connectFrame
argument_list|)
expr_stmt|;
name|String
name|f
init|=
name|stompConnection
operator|.
name|receiveFrame
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|startsWith
argument_list|(
literal|"CONNECTED"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|indexOf
argument_list|(
literal|"response-id:1"
argument_list|)
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hasCertificate
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

