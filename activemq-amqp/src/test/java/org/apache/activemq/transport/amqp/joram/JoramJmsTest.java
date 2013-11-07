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
name|amqp
operator|.
name|joram
package|;
end_package

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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|connection
operator|.
name|ConnectionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|connection
operator|.
name|TopicConnectionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|MessageBodyTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|MessageDefaultTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|MessageTypeTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|headers
operator|.
name|MessageHeaderTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|properties
operator|.
name|JMSXPropertyTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|properties
operator|.
name|MessagePropertyConversionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|properties
operator|.
name|MessagePropertyTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|queue
operator|.
name|QueueBrowserTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|queue
operator|.
name|TemporaryQueueTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|selector
operator|.
name|SelectorSyntaxTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|selector
operator|.
name|SelectorTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|session
operator|.
name|QueueSessionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|session
operator|.
name|SessionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|session
operator|.
name|UnifiedSessionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|topic
operator|.
name|TemporaryTopicTest
import|;
end_import

begin_class
specifier|public
class|class
name|JoramJmsTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
name|TestSuite
name|suite
init|=
operator|new
name|TestSuite
argument_list|()
decl_stmt|;
comment|// TODO: Fix these tests..
comment|// Fails due to
comment|// https://issues.apache.org/jira/browse/PROTON-154
comment|// suite.addTestSuite(TopicSessionTest.class);
comment|// Passing tests
name|suite
operator|.
name|addTestSuite
argument_list|(
name|MessageHeaderTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|QueueBrowserTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|MessageTypeTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// suite.addTestSuite(UnifiedSessionTest.class);   // https://issues.apache.org/jira/browse/AMQ-4375
name|suite
operator|.
name|addTestSuite
argument_list|(
name|TemporaryTopicTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|TopicConnectionTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SelectorSyntaxTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|QueueSessionTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SelectorTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|TemporaryQueueTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|ConnectionTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|SessionTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|JMSXPropertyTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|MessageBodyTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|MessageDefaultTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|MessagePropertyConversionTest
operator|.
name|class
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTestSuite
argument_list|(
name|MessagePropertyTest
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|suite
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|suite
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

