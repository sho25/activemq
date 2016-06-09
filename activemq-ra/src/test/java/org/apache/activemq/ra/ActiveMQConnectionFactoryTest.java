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
name|ra
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
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ObjectOutputStream
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
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSubscriber
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
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
name|ActiveMQConnection
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
name|ActiveMQTopicSubscriber
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_class
specifier|public
class|class
name|ActiveMQConnectionFactoryTest
block|{
specifier|private
name|ActiveMQManagedConnectionFactory
name|mcf
decl_stmt|;
specifier|private
name|ActiveMQConnectionRequestInfo
name|info
decl_stmt|;
specifier|private
name|String
name|url
init|=
literal|"vm://localhost?broker.persistent=false"
decl_stmt|;
specifier|private
name|String
name|user
init|=
literal|"defaultUser"
decl_stmt|;
specifier|private
name|String
name|pwd
init|=
literal|"defaultPasswd"
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|mcf
operator|=
operator|new
name|ActiveMQManagedConnectionFactory
argument_list|()
expr_stmt|;
name|info
operator|=
operator|new
name|ActiveMQConnectionRequestInfo
argument_list|()
expr_stmt|;
name|info
operator|.
name|setServerUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|info
operator|.
name|setUserName
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|info
operator|.
name|setPassword
argument_list|(
name|pwd
argument_list|)
expr_stmt|;
name|info
operator|.
name|setAllPrefetchValues
argument_list|(
operator|new
name|Integer
argument_list|(
literal|100
argument_list|)
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
name|testSerializability
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|mcf
argument_list|,
operator|new
name|ConnectionManagerAdapter
argument_list|()
argument_list|,
name|info
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ObjectOutputStream
name|oos
init|=
operator|new
name|ObjectOutputStream
argument_list|(
name|bos
argument_list|)
decl_stmt|;
name|oos
operator|.
name|writeObject
argument_list|(
name|factory
argument_list|)
expr_stmt|;
name|oos
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|byteArray
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|ObjectInputStream
name|ois
init|=
operator|new
name|ObjectInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|byteArray
argument_list|)
argument_list|)
decl_stmt|;
name|ActiveMQConnectionFactory
name|deserializedFactory
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|ois
operator|.
name|readObject
argument_list|()
decl_stmt|;
name|ois
operator|.
name|close
argument_list|()
expr_stmt|;
name|Connection
name|con
init|=
name|deserializedFactory
operator|.
name|createConnection
argument_list|(
literal|"defaultUser"
argument_list|,
literal|"defaultPassword"
argument_list|)
decl_stmt|;
name|ActiveMQConnection
name|connection
init|=
operator|(
call|(
name|ActiveMQConnection
call|)
argument_list|(
operator|(
name|ManagedConnectionProxy
operator|)
name|con
argument_list|)
operator|.
name|getManagedConnection
argument_list|()
operator|.
name|getPhysicalConnection
argument_list|()
operator|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|connection
operator|.
name|getPrefetchPolicy
argument_list|()
operator|.
name|getQueuePrefetch
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Connection object returned by ActiveMQConnectionFactory.createConnection() is null"
argument_list|,
name|con
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
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
name|testOptimizeDurablePrefetch
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQResourceAdapter
name|ra
init|=
operator|new
name|ActiveMQResourceAdapter
argument_list|()
decl_stmt|;
name|ra
operator|.
name|setServerUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|ra
operator|.
name|setUserName
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|ra
operator|.
name|setPassword
argument_list|(
name|pwd
argument_list|)
expr_stmt|;
name|ra
operator|.
name|setOptimizeDurableTopicPrefetch
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ra
operator|.
name|setDurableTopicPrefetch
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Connection
name|con
init|=
name|ra
operator|.
name|makeConnection
argument_list|()
decl_stmt|;
name|con
operator|.
name|setClientID
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|Session
name|sess
init|=
name|con
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|TopicSubscriber
name|sub
init|=
name|sess
operator|.
name|createDurableSubscriber
argument_list|(
name|sess
operator|.
name|createTopic
argument_list|(
literal|"TEST"
argument_list|)
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
name|con
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
operator|(
operator|(
name|ActiveMQTopicSubscriber
operator|)
name|sub
operator|)
operator|.
name|getPrefetchNumber
argument_list|()
argument_list|)
expr_stmt|;
name|con
operator|.
name|close
argument_list|()
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
name|testGetXAResource
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQResourceAdapter
name|ra
init|=
operator|new
name|ActiveMQResourceAdapter
argument_list|()
decl_stmt|;
name|ra
operator|.
name|setServerUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|ra
operator|.
name|setUserName
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|ra
operator|.
name|setPassword
argument_list|(
name|pwd
argument_list|)
expr_stmt|;
name|XAResource
index|[]
name|resources
init|=
name|ra
operator|.
name|getXAResources
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"one resource"
argument_list|,
literal|1
argument_list|,
name|resources
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no pending transactions"
argument_list|,
literal|0
argument_list|,
name|resources
index|[
literal|0
index|]
operator|.
name|recover
argument_list|(
literal|100
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// validate equality
name|XAResource
index|[]
name|resource2
init|=
name|ra
operator|.
name|getXAResources
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"one resource"
argument_list|,
literal|1
argument_list|,
name|resource2
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"isSameRM true"
argument_list|,
name|resources
index|[
literal|0
index|]
operator|.
name|isSameRM
argument_list|(
name|resource2
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"no tthe same instance"
argument_list|,
name|resources
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|resource2
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

