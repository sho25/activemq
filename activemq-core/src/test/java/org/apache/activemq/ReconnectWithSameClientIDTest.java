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
name|InvalidClientIDException
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
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ReconnectWithSameClientIDTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|boolean
name|transacted
decl_stmt|;
specifier|protected
name|int
name|authMode
init|=
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
decl_stmt|;
specifier|public
name|void
name|testReconnectMultipleTimesWithSameClientID
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|useConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
comment|// now lets create another which should fail
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|11
condition|;
name|i
operator|++
control|)
block|{
name|Connection
name|connection2
init|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|useConnection
argument_list|(
name|connection2
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown InvalidClientIDException on attempt"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidClientIDException
name|e
parameter_list|)
block|{
name|connection2
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Caught expected: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now lets try closing the original connection and creating a new connection with the same ID
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
name|connectionFactory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|useConnection
argument_list|(
name|connection
argument_list|)
expr_stmt|;
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
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|useConnection
parameter_list|(
name|Connection
name|connection
parameter_list|)
throws|throws
name|JMSException
block|{
name|connection
operator|.
name|setClientID
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
comment|/**          * Session session = connection.createSession(transacted, authMode);          * return session;          */
block|}
block|}
end_class

end_unit

