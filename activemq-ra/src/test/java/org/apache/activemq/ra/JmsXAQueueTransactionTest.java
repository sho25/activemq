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
name|DataOutputStream
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

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|resource
operator|.
name|spi
operator|.
name|ManagedConnection
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
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|Xid
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
name|ActiveMQPrefetchPolicy
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
name|JmsQueueTransactionTest
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
name|BrokerFactory
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

begin_class
specifier|public
class|class
name|JmsXAQueueTransactionTest
extends|extends
name|JmsQueueTransactionTest
block|{
specifier|private
name|ConnectionManagerAdapter
name|connectionManager
init|=
operator|new
name|ConnectionManagerAdapter
argument_list|()
decl_stmt|;
specifier|private
name|ActiveMQManagedConnectionFactory
name|managedConnectionFactory
decl_stmt|;
specifier|private
name|XAResource
name|xaResource
decl_stmt|;
specifier|private
specifier|static
name|long
name|txGenerator
decl_stmt|;
specifier|private
name|Xid
name|xid
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|BrokerFactory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://()/localhost?persistent=false&useJmx=false"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|setSessionTransacted
parameter_list|()
block|{
name|resourceProvider
operator|.
name|setTransacted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|resourceProvider
operator|.
name|setAckMode
argument_list|(
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ConnectionFactory
name|newConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
name|managedConnectionFactory
operator|=
operator|new
name|ActiveMQManagedConnectionFactory
argument_list|()
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setServerUrl
argument_list|(
literal|"vm://localhost?create=false&waitForStart=5000"
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setUserName
argument_list|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_USER
argument_list|)
expr_stmt|;
name|managedConnectionFactory
operator|.
name|setPassword
argument_list|(
name|ActiveMQConnectionFactory
operator|.
name|DEFAULT_PASSWORD
argument_list|)
expr_stmt|;
return|return
operator|(
name|ConnectionFactory
operator|)
name|managedConnectionFactory
operator|.
name|createConnectionFactory
argument_list|(
name|connectionManager
argument_list|)
return|;
block|}
comment|/**      * Recreates the connection.      *      * @throws javax.jms.JMSException      */
annotation|@
name|Override
specifier|protected
name|void
name|reconnect
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|reconnect
argument_list|()
expr_stmt|;
name|ManagedConnectionProxy
name|proxy
init|=
operator|(
name|ManagedConnectionProxy
operator|)
name|connection
decl_stmt|;
name|ManagedConnection
name|mc
init|=
name|proxy
operator|.
name|getManagedConnection
argument_list|()
decl_stmt|;
name|xaResource
operator|=
name|mc
operator|.
name|getXAResource
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ActiveMQPrefetchPolicy
name|getPrefetchPolicy
parameter_list|()
block|{
name|ManagedConnectionProxy
name|proxy
init|=
operator|(
name|ManagedConnectionProxy
operator|)
name|connection
decl_stmt|;
name|ActiveMQManagedConnection
name|mc
init|=
name|proxy
operator|.
name|getManagedConnection
argument_list|()
decl_stmt|;
name|ActiveMQConnection
name|conn
init|=
operator|(
name|ActiveMQConnection
operator|)
name|mc
operator|.
name|getPhysicalConnection
argument_list|()
decl_stmt|;
return|return
name|conn
operator|.
name|getPrefetchPolicy
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|beginTx
parameter_list|()
throws|throws
name|Exception
block|{
name|xid
operator|=
name|createXid
argument_list|()
expr_stmt|;
name|xaResource
operator|.
name|start
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMNOFLAGS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|commitTx
parameter_list|()
throws|throws
name|Exception
block|{
name|xaResource
operator|.
name|end
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|int
name|result
init|=
name|xaResource
operator|.
name|prepare
argument_list|(
name|xid
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
name|XAResource
operator|.
name|XA_OK
condition|)
block|{
name|xaResource
operator|.
name|commit
argument_list|(
name|xid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|xid
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|rollbackTx
parameter_list|()
throws|throws
name|Exception
block|{
name|xaResource
operator|.
name|end
argument_list|(
name|xid
argument_list|,
name|XAResource
operator|.
name|TMSUCCESS
argument_list|)
expr_stmt|;
name|xaResource
operator|.
name|rollback
argument_list|(
name|xid
argument_list|)
expr_stmt|;
name|xid
operator|=
literal|null
expr_stmt|;
block|}
comment|//This test won't work with xa tx it is overridden to do nothing here
annotation|@
name|Override
specifier|public
name|void
name|testMessageListener
parameter_list|()
throws|throws
name|Exception
block|{     }
comment|/**      * Sends a batch of messages and validates that the message sent before      * session close is not consumed.      *<p/>      * This test only works with local transactions, not xa. so its commented out here      *      * @throws Exception      */
annotation|@
name|Override
specifier|public
name|void
name|testSendSessionClose
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|Xid
name|createXid
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
operator|++
name|txGenerator
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|bs
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
operator|new
name|Xid
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|getFormatId
parameter_list|()
block|{
return|return
literal|86
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getGlobalTransactionId
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getBranchQualifier
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

