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
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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
name|TopicSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TransactionInProgressException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAQueueSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XATopicSession
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
name|command
operator|.
name|SessionId
import|;
end_import

begin_comment
comment|/**  * The XASession interface extends the capability of Session by adding access  * to a JMS provider's support for the  Java Transaction API (JTA) (optional).  * This support takes the form of a javax.transaction.xa.XAResource object.  * The functionality of this object closely resembles that defined by the  * standard X/Open XA Resource interface.  *<p/>  * An application server controls the transactional assignment of an XASession  * by obtaining its XAResource. It uses the XAResource to assign the session  * to a transaction, prepare and commit work on the transaction, and so on.  *<p/>  * An XAResource provides some fairly sophisticated facilities for  * interleaving work on multiple transactions, recovering a list of  * transactions in progress, and so on. A JTA aware JMS provider must fully  * implement this functionality. This could be done by using the services of a  * database that supports XA, or a JMS provider may choose to implement this  * functionality from scratch.  *<p/>  * A client of the application server is given what it thinks is a regular  * JMS Session. Behind the scenes, the application server controls the  * transaction management of the underlying XASession.  *<p/>  * The XASession interface is optional. JMS providers are not required to  * support this interface. This interface is for use by JMS providers to  * support transactional environments. Client programs are strongly encouraged  * to use the transactional support  available in their environment, rather  * than use these XA  interfaces directly.  *  *   * @see javax.jms.Session  * @see javax.jms.QueueSession  * @see javax.jms.TopicSession  * @see javax.jms.XASession  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQXASession
extends|extends
name|ActiveMQSession
implements|implements
name|QueueSession
implements|,
name|TopicSession
implements|,
name|XAQueueSession
implements|,
name|XATopicSession
block|{
specifier|public
name|ActiveMQXASession
parameter_list|(
name|ActiveMQXAConnection
name|connection
parameter_list|,
name|SessionId
name|sessionId
parameter_list|,
name|int
name|theAcknowlegeMode
parameter_list|,
name|boolean
name|dispatchAsync
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
argument_list|(
name|connection
argument_list|,
name|sessionId
argument_list|,
name|theAcknowlegeMode
argument_list|,
name|dispatchAsync
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|getTransacted
parameter_list|()
throws|throws
name|JMSException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
return|return
name|getTransactionContext
argument_list|()
operator|.
name|isInXATransaction
argument_list|()
return|;
block|}
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|JMSException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TransactionInProgressException
argument_list|(
literal|"Cannot rollback() inside an XASession"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|JMSException
block|{
name|checkClosed
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TransactionInProgressException
argument_list|(
literal|"Cannot commit() inside an XASession"
argument_list|)
throw|;
block|}
specifier|public
name|Session
name|getSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|this
return|;
block|}
specifier|public
name|XAResource
name|getXAResource
parameter_list|()
block|{
return|return
name|getTransactionContext
argument_list|()
return|;
block|}
specifier|public
name|QueueSession
name|getQueueSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|new
name|ActiveMQQueueSession
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|TopicSession
name|getTopicSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
operator|new
name|ActiveMQTopicSession
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/*      * when there is no XA transaction it is auto ack      */
specifier|public
name|boolean
name|isAutoAcknowledge
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|protected
name|void
name|doStartTransaction
parameter_list|()
throws|throws
name|JMSException
block|{
comment|// allow non transactional auto ack work on an XASession
comment|// Seems ok by the spec that an XAConnection can be used without an XA tx
block|}
block|}
end_class

end_unit

