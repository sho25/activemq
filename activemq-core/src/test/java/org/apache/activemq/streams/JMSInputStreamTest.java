begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|streams
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
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
name|javax
operator|.
name|jms
operator|.
name|Destination
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
name|JmsTestSupport
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
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * JMSInputStreamTest  */
end_comment

begin_class
specifier|public
class|class
name|JMSInputStreamTest
extends|extends
name|JmsTestSupport
block|{
specifier|protected
name|DataOutputStream
name|out
decl_stmt|;
specifier|protected
name|DataInputStream
name|in
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|connection2
decl_stmt|;
specifier|public
name|Destination
name|destination
decl_stmt|;
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
return|return
name|suite
argument_list|(
name|JMSInputStreamTest
operator|.
name|class
argument_list|)
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
specifier|public
name|void
name|initCombos
parameter_list|()
block|{
name|addCombinationValues
argument_list|(
literal|"destination"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|ActiveMQQueue
argument_list|(
literal|"TEST.QUEUE"
argument_list|)
block|,
operator|new
name|ActiveMQTopic
argument_list|(
literal|"TEST.TOPIC"
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
comment|/*      * @see TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setAutoFail
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|connection2
operator|=
operator|(
name|ActiveMQConnection
operator|)
name|factory
operator|.
name|createConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|connections
operator|.
name|add
argument_list|(
name|connection2
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|DataOutputStream
argument_list|(
name|connection
operator|.
name|createOutputStream
argument_list|(
name|destination
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
name|connection2
operator|.
name|createInputStream
argument_list|(
name|destination
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*      * @see TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testStreams
parameter_list|()
throws|throws
name|Exception
block|{
name|out
operator|.
name|writeInt
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
literal|2.3f
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readFloat
argument_list|()
operator|==
literal|2.3f
argument_list|)
expr_stmt|;
name|String
name|str
init|=
literal|"this is a test string"
decl_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|equals
argument_list|(
name|str
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|in
operator|.
name|readLong
argument_list|()
operator|==
name|i
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testLarge
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|TEST_DATA
init|=
literal|23
decl_stmt|;
specifier|final
name|int
name|DATA_LENGTH
init|=
literal|4096
decl_stmt|;
specifier|final
name|int
name|COUNT
init|=
literal|1024
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|DATA_LENGTH
index|]
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
name|TEST_DATA
expr_stmt|;
block|}
specifier|final
name|AtomicBoolean
name|complete
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Thread
name|runner
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|COUNT
condition|;
name|x
operator|++
control|)
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|2048
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|b
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
name|b
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|b
index|[
name|i
index|]
operator|==
name|TEST_DATA
argument_list|)
expr_stmt|;
block|}
block|}
name|complete
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|complete
init|)
block|{
name|complete
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|runner
operator|.
name|start
argument_list|()
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
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|complete
init|)
block|{
if|if
condition|(
operator|!
name|complete
operator|.
name|get
argument_list|()
condition|)
block|{
name|complete
operator|.
name|wait
argument_list|(
literal|30000
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|complete
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

