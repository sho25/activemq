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
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
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
name|ConnectionFactory
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
name|TextMessage
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
name|command
operator|.
name|ActiveMQMessage
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
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
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

begin_comment
comment|/**  * Useful base class for unit test cases  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TestSupport
extends|extends
name|TestCase
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
name|TestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|ConnectionFactory
name|connectionFactory
decl_stmt|;
specifier|protected
name|boolean
name|topic
init|=
literal|true
decl_stmt|;
specifier|public
name|TestSupport
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|TestSupport
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an ActiveMQMessage.      *       * @return ActiveMQMessage      */
specifier|protected
name|ActiveMQMessage
name|createMessage
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQMessage
argument_list|()
return|;
block|}
comment|/**      * Creates a destination.      *       * @param subject - topic or queue name.      * @return Destination - either an ActiveMQTopic or ActiveMQQUeue.      */
specifier|protected
name|Destination
name|createDestination
parameter_list|(
name|String
name|subject
parameter_list|)
block|{
if|if
condition|(
name|topic
condition|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|subject
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
name|subject
argument_list|)
return|;
block|}
block|}
comment|/**      * Tests if firstSet and secondSet are equal.      *       * @param messsage - string to be displayed when the assertion fails.      * @param firstSet[] - set of messages to be compared with its counterpart      *                in the secondset.      * @param secondSet[] - set of messages to be compared with its counterpart      *                in the firstset.      * @throws JMSException      */
specifier|protected
name|void
name|assertTextMessagesEqual
parameter_list|(
name|Message
index|[]
name|firstSet
parameter_list|,
name|Message
index|[]
name|secondSet
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertTextMessagesEqual
argument_list|(
literal|""
argument_list|,
name|firstSet
argument_list|,
name|secondSet
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests if firstSet and secondSet are equal.      *       * @param messsage - string to be displayed when the assertion fails.      * @param firstSet[] - set of messages to be compared with its counterpart      *                in the secondset.      * @param secondSet[] - set of messages to be compared with its counterpart      *                in the firstset.      */
specifier|protected
name|void
name|assertTextMessagesEqual
parameter_list|(
name|String
name|messsage
parameter_list|,
name|Message
index|[]
name|firstSet
parameter_list|,
name|Message
index|[]
name|secondSet
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertEquals
argument_list|(
literal|"Message count does not match: "
operator|+
name|messsage
argument_list|,
name|firstSet
operator|.
name|length
argument_list|,
name|secondSet
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|secondSet
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|m1
init|=
operator|(
name|TextMessage
operator|)
name|firstSet
index|[
name|i
index|]
decl_stmt|;
name|TextMessage
name|m2
init|=
operator|(
name|TextMessage
operator|)
name|secondSet
index|[
name|i
index|]
decl_stmt|;
name|assertTextMessageEqual
argument_list|(
literal|"Message "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" did not match : "
argument_list|,
name|m1
argument_list|,
name|m2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests if m1 and m2 are equal.      *       * @param m1 - message to be compared with m2.      * @param m2 - message to be compared with m1.      * @throws JMSException      */
specifier|protected
name|void
name|assertEquals
parameter_list|(
name|TextMessage
name|m1
parameter_list|,
name|TextMessage
name|m2
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|m1
argument_list|,
name|m2
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests if m1 and m2 are equal.      *       * @param message - string to be displayed when the assertion fails.      * @param m1 - message to be compared with m2.      * @param m2 - message to be compared with m1.      */
specifier|protected
name|void
name|assertTextMessageEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|TextMessage
name|m1
parameter_list|,
name|TextMessage
name|m2
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertFalse
argument_list|(
name|message
operator|+
literal|": expected {"
operator|+
name|m1
operator|+
literal|"}, but was {"
operator|+
name|m2
operator|+
literal|"}"
argument_list|,
name|m1
operator|==
literal|null
operator|^
name|m2
operator|==
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|m1
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|assertEquals
argument_list|(
name|message
argument_list|,
name|m1
operator|.
name|getText
argument_list|()
argument_list|,
name|m2
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests if m1 and m2 are equal.      *       * @param m1 - message to be compared with m2.      * @param m2 - message to be compared with m1.      * @throws JMSException      */
specifier|protected
name|void
name|assertEquals
parameter_list|(
name|Message
name|m1
parameter_list|,
name|Message
name|m2
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|m1
argument_list|,
name|m2
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests if m1 and m2 are equal.      *       * @param message - error message.      * @param m1 - message to be compared with m2.      * @param m2 -- message to be compared with m1.      */
specifier|protected
name|void
name|assertEquals
parameter_list|(
name|String
name|message
parameter_list|,
name|Message
name|m1
parameter_list|,
name|Message
name|m2
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertFalse
argument_list|(
name|message
operator|+
literal|": expected {"
operator|+
name|m1
operator|+
literal|"}, but was {"
operator|+
name|m2
operator|+
literal|"}"
argument_list|,
name|m1
operator|==
literal|null
operator|^
name|m2
operator|==
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|m1
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|assertTrue
argument_list|(
name|message
operator|+
literal|": expected {"
operator|+
name|m1
operator|+
literal|"}, but was {"
operator|+
name|m2
operator|+
literal|"}"
argument_list|,
name|m1
operator|.
name|getClass
argument_list|()
operator|==
name|m2
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|m1
operator|instanceof
name|TextMessage
condition|)
block|{
name|assertTextMessageEqual
argument_list|(
name|message
argument_list|,
operator|(
name|TextMessage
operator|)
name|m1
argument_list|,
operator|(
name|TextMessage
operator|)
name|m2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|message
argument_list|,
name|m1
argument_list|,
name|m2
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test if base directory contains spaces      */
specifier|protected
name|void
name|assertBaseDirectoryContainsSpaces
parameter_list|()
block|{
name|assertFalse
argument_list|(
literal|"Base directory cannot contain spaces."
argument_list|,
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"basedir"
argument_list|,
literal|"."
argument_list|)
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates an ActiveMQConnectionFactory.      *       * @return ActiveMQConnectionFactory      * @throws Exception      */
specifier|protected
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
return|;
block|}
comment|/**      * Factory method to create a new connection.      *       * @return connection      * @throws Exception      */
specifier|protected
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|getConnectionFactory
argument_list|()
operator|.
name|createConnection
argument_list|()
return|;
block|}
comment|/**      * Creates an ActiveMQ connection factory.      *       * @return connectionFactory      * @throws Exception      */
specifier|public
name|ConnectionFactory
name|getConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connectionFactory
operator|==
literal|null
condition|)
block|{
name|connectionFactory
operator|=
name|createConnectionFactory
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should have created a connection factory!"
argument_list|,
name|connectionFactory
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|connectionFactory
return|;
block|}
comment|/**      * Returns the consumer subject.      *       * @return String      */
specifier|protected
name|String
name|getConsumerSubject
parameter_list|()
block|{
return|return
name|getSubject
argument_list|()
return|;
block|}
comment|/**      * Returns the producer subject.      *       * @return String      */
specifier|protected
name|String
name|getProducerSubject
parameter_list|()
block|{
return|return
name|getSubject
argument_list|()
return|;
block|}
comment|/**      * Returns the subject.      *       * @return String      */
specifier|protected
name|String
name|getSubject
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|getName
argument_list|()
return|;
block|}
specifier|protected
name|void
name|assertArrayEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
index|[]
name|expected
parameter_list|,
name|Object
index|[]
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|". Array length"
argument_list|,
name|expected
operator|.
name|length
argument_list|,
name|actual
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expected
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|". element: "
operator|+
name|i
argument_list|,
name|expected
index|[
name|i
index|]
argument_list|,
name|actual
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertPrimitiveArrayEqual
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
name|expected
parameter_list|,
name|Object
name|actual
parameter_list|)
block|{
name|int
name|length
init|=
name|Array
operator|.
name|getLength
argument_list|(
name|expected
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|message
operator|+
literal|". Array length"
argument_list|,
name|length
argument_list|,
name|Array
operator|.
name|getLength
argument_list|(
name|actual
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|message
operator|+
literal|". element: "
operator|+
name|i
argument_list|,
name|Array
operator|.
name|get
argument_list|(
name|expected
argument_list|,
name|i
argument_list|)
argument_list|,
name|Array
operator|.
name|get
argument_list|(
name|actual
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

