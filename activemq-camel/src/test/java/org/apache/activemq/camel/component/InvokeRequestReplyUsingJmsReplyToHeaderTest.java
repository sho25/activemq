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
name|camel
operator|.
name|component
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|camel
operator|.
name|component
operator|.
name|ActiveMQComponent
operator|.
name|activeMQComponent
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasEntry
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasKey
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
name|List
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
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|ActiveMQDestination
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
name|ActiveMQQueue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|CamelContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|ContextTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Exchange
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Headers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Processor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|builder
operator|.
name|RouteBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|mock
operator|.
name|AssertionClause
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|mock
operator|.
name|MockEndpoint
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
name|InvokeRequestReplyUsingJmsReplyToHeaderTest
extends|extends
name|ContextTestSupport
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ActiveMQReplyToHeaderUsingConverterTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|replyQueueName
init|=
literal|"queue://test.reply"
decl_stmt|;
specifier|protected
name|Object
name|correlationID
init|=
literal|"ABC-123"
decl_stmt|;
specifier|protected
name|Object
name|groupID
init|=
literal|"GROUP-XYZ"
decl_stmt|;
specifier|private
name|MyServer
name|myBean
init|=
operator|new
name|MyServer
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testPerformRequestReplyOverJms
parameter_list|()
throws|throws
name|Exception
block|{
name|MockEndpoint
name|resultEndpoint
init|=
name|getMockEndpoint
argument_list|(
literal|"mock:result"
argument_list|)
decl_stmt|;
name|resultEndpoint
operator|.
name|expectedBodiesReceived
argument_list|(
literal|"Hello James"
argument_list|)
expr_stmt|;
name|AssertionClause
name|firstMessage
init|=
name|resultEndpoint
operator|.
name|message
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|firstMessage
operator|.
name|header
argument_list|(
literal|"JMSCorrelationID"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|correlationID
argument_list|)
expr_stmt|;
comment|/*         TODO - allow JMS headers to be copied?          firstMessage.header("cheese").isEqualTo(123);         firstMessage.header("JMSXGroupID").isEqualTo(groupID);         firstMessage.header("JMSReplyTo").isEqualTo(ActiveMQConverter.toDestination(replyQueueName)); */
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"cheese"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSReplyTo"
argument_list|,
name|replyQueueName
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSCorrelationID"
argument_list|,
name|correlationID
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSXGroupID"
argument_list|,
name|groupID
argument_list|)
expr_stmt|;
comment|// Camel 2.0 ignores JMSReplyTo, so we're using replyTo MEP property
name|template
operator|.
name|request
argument_list|(
literal|"activemq:test.server?replyTo=queue:test.reply"
argument_list|,
operator|new
name|Processor
argument_list|()
block|{
specifier|public
name|void
name|process
parameter_list|(
name|Exchange
name|exchange
parameter_list|)
block|{
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|setBody
argument_list|(
literal|"James"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"cheese"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSReplyTo"
argument_list|,
name|replyQueueName
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSCorrelationID"
argument_list|,
name|correlationID
argument_list|)
expr_stmt|;
name|headers
operator|.
name|put
argument_list|(
literal|"JMSXGroupID"
argument_list|,
name|groupID
argument_list|)
expr_stmt|;
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|setHeaders
argument_list|(
name|headers
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|resultEndpoint
operator|.
name|assertIsSatisfied
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|Exchange
argument_list|>
name|list
init|=
name|resultEndpoint
operator|.
name|getReceivedExchanges
argument_list|()
decl_stmt|;
name|Exchange
name|exchange
init|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Message
name|in
init|=
name|exchange
operator|.
name|getIn
argument_list|()
decl_stmt|;
name|Object
name|replyTo
init|=
name|in
operator|.
name|getHeader
argument_list|(
literal|"JMSReplyTo"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reply to is: "
operator|+
name|replyTo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received headers: "
operator|+
name|in
operator|.
name|getHeaders
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received body: "
operator|+
name|in
operator|.
name|getBody
argument_list|()
argument_list|)
expr_stmt|;
name|assertMessageHeader
argument_list|(
name|in
argument_list|,
literal|"JMSCorrelationID"
argument_list|,
name|correlationID
argument_list|)
expr_stmt|;
comment|/*         TODO         Destination destination = assertIsInstanceOf(Destination.class, replyTo);         assertEquals("ReplyTo", replyQueueName, destination.toString());         assertMessageHeader(in, "cheese", 123);         assertMessageHeader(in, "JMSXGroupID", groupID);         */
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|receivedHeaders
init|=
name|myBean
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|receivedHeaders
argument_list|,
name|hasKey
argument_list|(
literal|"JMSReplyTo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|receivedHeaders
argument_list|,
name|hasEntry
argument_list|(
literal|"JMSXGroupID"
argument_list|,
name|groupID
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|receivedHeaders
argument_list|,
name|hasEntry
argument_list|(
literal|"JMSCorrelationID"
argument_list|,
name|correlationID
argument_list|)
argument_list|)
expr_stmt|;
name|replyTo
operator|=
name|receivedHeaders
operator|.
name|get
argument_list|(
literal|"JMSReplyTo"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Reply to is: "
operator|+
name|replyTo
argument_list|)
expr_stmt|;
name|Destination
name|destination
init|=
name|assertIsInstanceOf
argument_list|(
name|Destination
operator|.
name|class
argument_list|,
name|replyTo
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ReplyTo"
argument_list|,
name|replyQueueName
argument_list|,
name|destination
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|CamelContext
name|createCamelContext
parameter_list|()
throws|throws
name|Exception
block|{
name|CamelContext
name|camelContext
init|=
name|super
operator|.
name|createCamelContext
argument_list|()
decl_stmt|;
comment|// START SNIPPET: example
name|camelContext
operator|.
name|addComponent
argument_list|(
literal|"activemq"
argument_list|,
name|activeMQComponent
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
argument_list|)
expr_stmt|;
comment|// END SNIPPET: example
return|return
name|camelContext
return|;
block|}
specifier|protected
name|RouteBuilder
name|createRouteBuilder
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|RouteBuilder
argument_list|()
block|{
specifier|public
name|void
name|configure
parameter_list|()
throws|throws
name|Exception
block|{
name|from
argument_list|(
literal|"activemq:test.server"
argument_list|)
operator|.
name|bean
argument_list|(
name|myBean
argument_list|)
expr_stmt|;
name|from
argument_list|(
literal|"activemq:test.reply"
argument_list|)
operator|.
name|to
argument_list|(
literal|"mock:result"
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|protected
specifier|static
class|class
name|MyServer
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
decl_stmt|;
specifier|public
name|String
name|process
parameter_list|(
annotation|@
name|Headers
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
parameter_list|,
name|String
name|body
parameter_list|)
block|{
name|this
operator|.
name|headers
operator|=
name|headers
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"process() invoked with headers: "
operator|+
name|headers
argument_list|)
expr_stmt|;
return|return
literal|"Hello "
operator|+
name|body
return|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getHeaders
parameter_list|()
block|{
return|return
name|headers
return|;
block|}
block|}
block|}
end_class

end_unit

