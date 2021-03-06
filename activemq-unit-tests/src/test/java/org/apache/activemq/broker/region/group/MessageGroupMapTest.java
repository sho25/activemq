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
name|broker
operator|.
name|region
operator|.
name|group
package|;
end_package

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
name|command
operator|.
name|ConnectionId
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
name|ConsumerId
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
comment|/**  *   *   */
end_comment

begin_class
specifier|public
class|class
name|MessageGroupMapTest
extends|extends
name|TestCase
block|{
specifier|protected
name|MessageGroupMap
name|map
decl_stmt|;
specifier|private
name|ConsumerId
name|consumer1
decl_stmt|;
specifier|private
name|ConsumerId
name|consumer2
decl_stmt|;
specifier|private
name|ConsumerId
name|consumer3
decl_stmt|;
specifier|private
name|long
name|idCounter
decl_stmt|;
specifier|public
name|void
name|testSingleConsumerForManyBucks
parameter_list|()
throws|throws
name|Exception
block|{
name|assertGet
argument_list|(
literal|"1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"1"
argument_list|,
name|consumer1
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"1"
argument_list|,
name|consumer1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"2"
argument_list|,
name|consumer1
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"2"
argument_list|,
name|consumer1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"3"
argument_list|,
name|consumer1
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"3"
argument_list|,
name|consumer1
argument_list|)
expr_stmt|;
name|MessageGroupSet
name|set
init|=
name|map
operator|.
name|removeConsumer
argument_list|(
name|consumer1
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
name|set
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|set
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|set
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"2"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"3"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testManyConsumers
parameter_list|()
throws|throws
name|Exception
block|{
name|assertGet
argument_list|(
literal|"1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"1"
argument_list|,
name|consumer1
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"1"
argument_list|,
name|consumer1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"2"
argument_list|,
name|consumer2
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"2"
argument_list|,
name|consumer2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"3"
argument_list|,
name|consumer3
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"3"
argument_list|,
name|consumer3
argument_list|)
expr_stmt|;
name|MessageGroupSet
name|set
init|=
name|map
operator|.
name|removeConsumer
argument_list|(
name|consumer1
argument_list|)
decl_stmt|;
name|assertContains
argument_list|(
name|set
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"1"
argument_list|,
name|consumer2
argument_list|)
expr_stmt|;
name|assertGet
argument_list|(
literal|"1"
argument_list|,
name|consumer2
argument_list|)
expr_stmt|;
name|set
operator|=
name|map
operator|.
name|removeConsumer
argument_list|(
name|consumer2
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|set
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|assertContains
argument_list|(
name|set
argument_list|,
literal|"2"
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
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|map
operator|=
name|createMessageGroupMap
argument_list|()
expr_stmt|;
name|consumer1
operator|=
name|createConsumerId
argument_list|()
expr_stmt|;
name|consumer2
operator|=
name|createConsumerId
argument_list|()
expr_stmt|;
name|consumer3
operator|=
name|createConsumerId
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|MessageGroupMap
name|createMessageGroupMap
parameter_list|()
block|{
return|return
operator|new
name|SimpleMessageGroupMap
argument_list|()
return|;
block|}
specifier|protected
name|ConsumerId
name|createConsumerId
parameter_list|()
block|{
name|ConnectionId
name|connectionId
init|=
operator|new
name|ConnectionId
argument_list|(
literal|""
operator|+
operator|++
name|idCounter
argument_list|)
decl_stmt|;
name|SessionId
name|sessionId
init|=
operator|new
name|SessionId
argument_list|(
name|connectionId
argument_list|,
operator|++
name|idCounter
argument_list|)
decl_stmt|;
name|ConsumerId
name|answer
init|=
operator|new
name|ConsumerId
argument_list|(
name|sessionId
argument_list|,
operator|++
name|idCounter
argument_list|)
decl_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|void
name|assertGet
parameter_list|(
name|String
name|groupdId
parameter_list|,
name|ConsumerId
name|expected
parameter_list|)
block|{
name|ConsumerId
name|actual
init|=
name|map
operator|.
name|get
argument_list|(
name|groupdId
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Entry for groupId: "
operator|+
name|groupdId
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertContains
parameter_list|(
name|MessageGroupSet
name|set
parameter_list|,
name|String
name|groupID
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"MessageGroup set: "
operator|+
name|set
operator|+
literal|" does not contain groupID: "
operator|+
name|groupID
argument_list|,
name|set
operator|.
name|contains
argument_list|(
name|groupID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

