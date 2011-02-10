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
name|store
operator|.
name|jdbc
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
name|sql
operator|.
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|DeliveryMode
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XASession
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
name|XAException
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
name|ActiveMQXAConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_comment
comment|// https://issues.apache.org/activemq/browse/AMQ-2880
end_comment

begin_class
specifier|public
class|class
name|JDBCXACommitExceptionTest
extends|extends
name|JDBCCommitExceptionTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JDBCXACommitExceptionTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|txGenerator
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|protected
name|ActiveMQXAConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"tcp://localhost:61616?jms.prefetchPolicy.all=0&jms.redeliveryPolicy.maximumRedeliveries="
operator|+
name|messagesExpected
argument_list|)
decl_stmt|;
name|boolean
name|onePhase
init|=
literal|true
decl_stmt|;
specifier|public
name|void
name|testTwoPhaseSqlException
parameter_list|()
throws|throws
name|Exception
block|{
name|onePhase
operator|=
literal|false
expr_stmt|;
name|doTestSqlException
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|receiveMessages
parameter_list|(
name|int
name|messagesExpected
parameter_list|)
throws|throws
name|Exception
block|{
name|XAConnection
name|connection
init|=
name|factory
operator|.
name|createXAConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|XASession
name|session
init|=
name|connection
operator|.
name|createXASession
argument_list|()
decl_stmt|;
name|jdbc
operator|.
name|setShouldBreak
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// first try and receive these messages, they'll continually fail
name|receiveMessages
argument_list|(
name|messagesExpected
argument_list|,
name|session
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
name|jdbc
operator|.
name|setShouldBreak
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// now that the store is sane, try and get all the messages sent
return|return
name|receiveMessages
argument_list|(
name|messagesExpected
argument_list|,
name|session
argument_list|,
name|onePhase
argument_list|)
return|;
block|}
specifier|protected
name|int
name|receiveMessages
parameter_list|(
name|int
name|messagesExpected
parameter_list|,
name|XASession
name|session
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|messagesReceived
init|=
literal|0
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
name|messagesExpected
condition|;
name|i
operator|++
control|)
block|{
name|Destination
name|destination
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"TEST"
argument_list|)
decl_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|XAResource
name|resource
init|=
name|session
operator|.
name|getXAResource
argument_list|()
decl_stmt|;
name|resource
operator|.
name|recover
argument_list|(
name|XAResource
operator|.
name|TMSTARTRSCAN
argument_list|)
expr_stmt|;
name|resource
operator|.
name|recover
argument_list|(
name|XAResource
operator|.
name|TMNOFLAGS
argument_list|)
expr_stmt|;
name|Xid
name|tid
init|=
name|createXid
argument_list|()
decl_stmt|;
name|Message
name|message
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Receiving message "
operator|+
operator|(
name|messagesReceived
operator|+
literal|1
operator|)
operator|+
literal|" of "
operator|+
name|messagesExpected
argument_list|)
expr_stmt|;
name|resource
operator|.
name|start
argument_list|(
name|tid
argument_list|,
name|XAResource
operator|.
name|TMNOFLAGS
argument_list|)
expr_stmt|;
name|message
operator|=
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received : "
operator|+
name|message
argument_list|)
expr_stmt|;
name|resource
operator|.
name|end
argument_list|(
name|tid
argument_list|,
name|XAResource
operator|.
name|TMSUCCESS
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|onePhase
condition|)
block|{
name|resource
operator|.
name|commit
argument_list|(
name|tid
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resource
operator|.
name|prepare
argument_list|(
name|tid
argument_list|)
expr_stmt|;
name|resource
operator|.
name|commit
argument_list|(
name|tid
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|messagesReceived
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caught exception:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Rolling back transaction (just in case, no need to do this as it is implicit in a 1pc commit failure) "
operator|+
name|tid
argument_list|)
expr_stmt|;
name|resource
operator|.
name|rollback
argument_list|(
name|tid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XAException
name|ex
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Caught exception during rollback: "
operator|+
name|ex
operator|+
literal|" forgetting transaction "
operator|+
name|tid
argument_list|)
expr_stmt|;
name|resource
operator|.
name|forget
argument_list|(
name|tid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XAException
name|ex1
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"rollback/forget failed: "
operator|+
name|ex1
operator|.
name|errorCode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|consumer
operator|!=
literal|null
condition|)
block|{
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|messagesReceived
return|;
block|}
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
specifier|public
name|int
name|getFormatId
parameter_list|()
block|{
return|return
literal|86
return|;
block|}
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

