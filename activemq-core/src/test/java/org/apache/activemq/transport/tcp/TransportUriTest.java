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
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|ActiveMQConnectionFactory
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
name|EmbeddedBrokerTestSupport
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
name|BrokerService
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

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|TransportUriTest
extends|extends
name|EmbeddedBrokerTestSupport
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
name|TransportUriTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|public
name|String
name|prefix
decl_stmt|;
specifier|public
name|String
name|postfix
decl_stmt|;
specifier|public
name|void
name|initCombosForTestUriOptionsWork
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"prefix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|""
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"postfix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"?tcpNoDelay=true&keepAlive=true"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUriOptionsWork
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
name|prefix
operator|+
name|bindAddress
operator|+
name|postfix
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting via: "
operator|+
name|uri
argument_list|)
expr_stmt|;
name|connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|initCombosForTestBadVersionNumberDoesNotWork
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"prefix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|""
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"postfix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"?tcpNoDelay=true&keepAlive=true"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBadVersionNumberDoesNotWork
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
name|prefix
operator|+
name|bindAddress
operator|+
name|postfix
operator|+
literal|"&minmumWireFormatVersion=65535"
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting via: "
operator|+
name|uri
argument_list|)
expr_stmt|;
try|try
block|{
name|connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an exception!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{         }
block|}
specifier|public
name|void
name|initCombosForTestBadPropertyNameFails
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"prefix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|""
block|}
argument_list|)
expr_stmt|;
name|addCombinationValues
argument_list|(
literal|"postfix"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"?tcpNoDelay=true&keepAlive=true"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBadPropertyNameFails
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
name|prefix
operator|+
name|bindAddress
operator|+
name|postfix
operator|+
literal|"&cheese=abc"
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting via: "
operator|+
name|uri
argument_list|)
expr_stmt|;
try|try
block|{
name|connection
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|uri
argument_list|)
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an exception!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|expected
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|bindAddress
operator|=
literal|"tcp://localhost:61616"
expr_stmt|;
name|super
operator|.
name|setUp
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
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
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
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|answer
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setUseJmx
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setPersistent
argument_list|(
name|isPersistent
argument_list|()
argument_list|)
expr_stmt|;
name|answer
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|TransportUriTest
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

