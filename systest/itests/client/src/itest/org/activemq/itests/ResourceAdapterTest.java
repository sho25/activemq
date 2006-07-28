begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|itests
package|;
end_package

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|CreateException
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
name|naming
operator|.
name|InitialContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
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
name|activemq
operator|.
name|itest
operator|.
name|ejb
operator|.
name|JMSToolHome
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|itest
operator|.
name|ejb
operator|.
name|JMSTool
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ResourceAdapterTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|String
name|JMSBEAN_JNDI
init|=
literal|"org/activemq/itest/JMSToolBean"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_QUEUE
init|=
literal|"TestQueue"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRANSFER_MDB_INPUT_QUEUE
init|=
literal|"MDBInQueue"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TRANSFER_MDB_OUTPUT_QUEUE
init|=
literal|"MDBOutQueue"
decl_stmt|;
specifier|private
name|JMSTool
name|jmsTool
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|InitialContext
name|ctx
init|=
name|createInitialContext
argument_list|()
decl_stmt|;
name|JMSToolHome
name|home
init|=
operator|(
name|JMSToolHome
operator|)
name|ctx
operator|.
name|lookup
argument_list|(
name|JMSBEAN_JNDI
argument_list|)
decl_stmt|;
name|jmsTool
operator|=
name|home
operator|.
name|create
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
name|jmsTool
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|jmsTool
operator|.
name|drain
argument_list|(
name|TEST_QUEUE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{             }
block|}
block|}
specifier|public
name|void
name|testSendReceiveMultiple
parameter_list|()
throws|throws
name|CreateException
throws|,
name|RemoteException
throws|,
name|NamingException
throws|,
name|JMSException
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|String
name|msg1
init|=
literal|"Test Send Receive:"
operator|+
name|i
decl_stmt|;
name|jmsTool
operator|.
name|sendTextMessage
argument_list|(
name|TEST_QUEUE
argument_list|,
name|msg1
argument_list|)
expr_stmt|;
name|String
name|msg2
init|=
name|jmsTool
operator|.
name|receiveTextMessage
argument_list|(
name|TEST_QUEUE
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Message are not the same a iteration: "
operator|+
name|i
argument_list|,
name|msg1
argument_list|,
name|msg2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * The MDBTransferBean should be moving message from the input queue to the output queue.      * Check to see if message sent to it's input get to the output queue.       */
specifier|public
name|void
name|testSendReceiveFromMDB
parameter_list|()
throws|throws
name|CreateException
throws|,
name|RemoteException
throws|,
name|NamingException
throws|,
name|JMSException
block|{
name|HashSet
name|a
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|HashSet
name|b
init|=
operator|new
name|HashSet
argument_list|()
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|String
name|msg1
init|=
literal|"Test Send Receive From MDB:"
operator|+
name|i
decl_stmt|;
name|a
operator|.
name|add
argument_list|(
name|msg1
argument_list|)
expr_stmt|;
name|jmsTool
operator|.
name|sendTextMessage
argument_list|(
name|TRANSFER_MDB_INPUT_QUEUE
argument_list|,
name|msg1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|String
name|msg2
init|=
name|jmsTool
operator|.
name|receiveTextMessage
argument_list|(
name|TRANSFER_MDB_OUTPUT_QUEUE
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|b
operator|.
name|add
argument_list|(
name|msg2
argument_list|)
expr_stmt|;
block|}
comment|// Compare the messages using sets since they may be received out of order since,
comment|// the MDB runns concurrent threads.
name|assertEquals
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
specifier|private
name|InitialContext
name|createInitialContext
parameter_list|()
throws|throws
name|NamingException
block|{
name|Hashtable
name|props
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"java.naming.factory.initial"
argument_list|,
literal|"org.openejb.client.RemoteInitialContextFactory"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"java.naming.provider.url"
argument_list|,
literal|"127.0.0.1:4201"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"java.naming.security.principal"
argument_list|,
literal|"testuser"
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"java.naming.security.credentials"
argument_list|,
literal|"testpassword"
argument_list|)
expr_stmt|;
return|return
operator|new
name|InitialContext
argument_list|(
name|props
argument_list|)
return|;
block|}
block|}
end_class

end_unit

