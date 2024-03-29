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
name|filter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Set
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
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
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

begin_class
specifier|public
class|class
name|DestinationMapTest
extends|extends
name|TestCase
block|{
specifier|protected
name|DestinationMap
name|map
init|=
operator|new
name|DestinationMap
argument_list|()
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|d1
init|=
name|createDestination
argument_list|(
literal|"TEST.D1"
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|d2
init|=
name|createDestination
argument_list|(
literal|"TEST.BAR.D2"
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|d3
init|=
name|createDestination
argument_list|(
literal|"TEST.BAR.D3"
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|compositeDestination1
init|=
name|createDestination
argument_list|(
literal|"TEST.D1,TEST.BAR.D2"
argument_list|)
decl_stmt|;
specifier|protected
name|ActiveMQDestination
name|compositeDestination2
init|=
name|createDestination
argument_list|(
literal|"TEST.D1,TEST.BAR.D3"
argument_list|)
decl_stmt|;
specifier|protected
name|Object
name|v1
init|=
literal|"value1"
decl_stmt|;
specifier|protected
name|Object
name|v2
init|=
literal|"value2"
decl_stmt|;
specifier|protected
name|Object
name|v3
init|=
literal|"value3"
decl_stmt|;
specifier|protected
name|Object
name|v4
init|=
literal|"value4"
decl_stmt|;
specifier|protected
name|Object
name|v5
init|=
literal|"value5"
decl_stmt|;
specifier|protected
name|Object
name|v6
init|=
literal|"value6"
decl_stmt|;
specifier|public
name|void
name|testCompositeDestinations
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQDestination
name|d1
init|=
name|createDestination
argument_list|(
literal|"TEST.BAR.D2"
argument_list|)
decl_stmt|;
name|ActiveMQDestination
name|d2
init|=
name|createDestination
argument_list|(
literal|"TEST.BAR.D3"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d1
argument_list|,
name|d1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d2
argument_list|,
name|d2
argument_list|)
expr_stmt|;
name|map
operator|.
name|get
argument_list|(
name|createDestination
argument_list|(
literal|"TEST.BAR.D2,TEST.BAR.D3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSimpleDestinations
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|put
argument_list|(
name|d1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d2
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d3
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|d1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|d2
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|d3
argument_list|,
name|v3
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testQueueAndTopicWithSameName
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQQueue
name|q1
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|ActiveMQTopic
name|t1
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|q1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|t1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|q1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|t1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSimpleDestinationsWithMultipleValues
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|put
argument_list|(
name|d1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d2
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|d1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D2"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|d3
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSimpleAndCompositeDestinations
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|put
argument_list|(
name|d1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|compositeDestination1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|compositeDestination2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|d2
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|d3
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|compositeDestination1
operator|.
name|toString
argument_list|()
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
name|compositeDestination2
operator|.
name|toString
argument_list|()
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|compositeDestination1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|compositeDestination2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLookupOneStepWildcardDestinations
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|put
argument_list|(
name|d1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d2
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d3
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.D1"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D2"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.D2"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.BAR.D2"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*.D2"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D3"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.D3"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.BAR.D3"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*.D3"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D4"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLookupMultiStepWildcardDestinations
parameter_list|()
throws|throws
name|Exception
block|{
name|map
operator|.
name|put
argument_list|(
name|d1
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d2
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|d3
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|allValues
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|v1
block|,
name|v2
block|,
name|v3
block|}
argument_list|)
decl_stmt|;
name|assertMapValue
argument_list|(
literal|">"
argument_list|,
name|allValues
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.>"
argument_list|,
name|allValues
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.>"
argument_list|,
name|allValues
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.>"
argument_list|,
name|allValues
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.*.>"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO.>"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStoreWildcardWithOneStepPath
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.BAR.D3"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.FOO"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.FOO"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D3"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.D1"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.BAR.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.BAR.D3"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*.D3"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStoreWildcardInMiddleOfPath
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.XYZ.D3"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.XYZ.D4"
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.BAR.D3"
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.*.D2"
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.D3"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.D4"
argument_list|,
name|v2
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.>"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.>"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.>.>"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*.D3"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
name|v2
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D2"
argument_list|,
name|v2
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.D2"
argument_list|,
name|v2
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
name|v2
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDoubleWildcardDoesNotMatchLongerPattern
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.BAR.D3"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*.D3"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testWildcardAtEndOfPathAndAtBeginningOfSearch
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.D1"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAnyPathWildcardInMap
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"TEST.FOO.>"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.BAR.WHANOT.A.B.C"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.BAR.WHANOT"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.BAR"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.*"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSimpleAddRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Root child count"
argument_list|,
literal|1
argument_list|,
name|map
operator|.
name|getTopicRootChildCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|remove
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Root child count"
argument_list|,
literal|0
argument_list|,
name|map
operator|.
name|getTopicRootChildCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMQTTMappedWildcards
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"TopicA"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|".TopicA"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TopicA."
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"."
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"..TopicA"
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|".."
argument_list|,
name|v6
argument_list|)
expr_stmt|;
comment|// test wildcard patterns "#", "+", "+/#", "/+", "+/", "+/+", "+/+/", "+/+/+"
name|assertMapValue
argument_list|(
literal|">"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.>"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|".*"
argument_list|,
name|v2
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*."
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*."
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*.*"
argument_list|,
name|v5
argument_list|,
name|v6
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStoreAndLookupAllWildcards
parameter_list|()
throws|throws
name|Exception
block|{
name|loadSample2
argument_list|()
expr_stmt|;
name|assertSample2
argument_list|()
expr_stmt|;
comment|// lets remove everything and add it back
name|remove
argument_list|(
literal|"TEST.FOO"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|remove
argument_list|(
literal|"TEST.XYZ"
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|remove
argument_list|(
literal|"TEST.*"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|remove
argument_list|(
literal|">"
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|remove
argument_list|(
literal|"TEST.>"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|remove
argument_list|(
literal|"TEST.FOO.BAR"
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.FOO"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.FOO"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.BAR"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D3"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.D1"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|loadSample2
argument_list|()
expr_stmt|;
name|assertSample2
argument_list|()
expr_stmt|;
name|remove
argument_list|(
literal|">"
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|remove
argument_list|(
literal|"TEST.*"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO"
argument_list|,
name|v1
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.FOO"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.FOO"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.BAR"
argument_list|,
name|v3
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D3"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|,
name|v3
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v1
argument_list|,
name|v3
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.D1"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.*"
argument_list|,
name|v3
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAddAndRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"FOO.A"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO.>"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"FOO.B"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO.>"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|removeAll
argument_list|(
name|createDestination
argument_list|(
literal|"FOO.A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO.>"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testRemoveWildcard
parameter_list|()
throws|throws
name|Exception
block|{
name|put
argument_list|(
literal|"FOO.A"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"FOO.>"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|removeAll
argument_list|(
name|createDestination
argument_list|(
literal|"FOO.>"
argument_list|)
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO.A"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"FOO.A"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"FOO.>"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|createDestination
argument_list|(
literal|"FOO.>"
argument_list|)
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"FOO.A"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|loadSample2
parameter_list|()
block|{
name|put
argument_list|(
literal|"TEST.FOO"
argument_list|,
name|v1
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.*"
argument_list|,
name|v2
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.>"
argument_list|,
name|v3
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|">"
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.FOO.BAR"
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|put
argument_list|(
literal|"TEST.XYZ"
argument_list|,
name|v6
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertSample2
parameter_list|()
block|{
name|assertMapValue
argument_list|(
literal|"FOO"
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.D1"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.FOO"
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.FOO"
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.FOO.BAR"
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.D3"
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.*"
argument_list|,
name|v1
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v6
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"*.D1"
argument_list|,
name|v2
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.*.*"
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|,
name|v5
argument_list|)
expr_stmt|;
name|assertMapValue
argument_list|(
literal|"TEST.BAR.*"
argument_list|,
name|v3
argument_list|,
name|v4
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|put
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|createDestination
argument_list|(
name|name
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|remove
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|ActiveMQDestination
name|destination
init|=
name|createDestination
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|destination
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMapValue
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|Object
name|expected
parameter_list|)
block|{
name|ActiveMQDestination
name|destination
init|=
name|createDestination
argument_list|(
name|destinationName
argument_list|)
decl_stmt|;
name|assertMapValue
argument_list|(
name|destination
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMapValue
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|Object
name|expected1
parameter_list|,
name|Object
name|expected2
parameter_list|)
block|{
name|assertMapValue
argument_list|(
name|destinationName
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|expected1
block|,
name|expected2
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMapValue
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|Object
name|expected1
parameter_list|,
name|Object
name|expected2
parameter_list|,
name|Object
name|expected3
parameter_list|)
block|{
name|assertMapValue
argument_list|(
name|destinationName
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|expected1
block|,
name|expected2
block|,
name|expected3
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMapValue
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|Object
name|expected1
parameter_list|,
name|Object
name|expected2
parameter_list|,
name|Object
name|expected3
parameter_list|,
name|Object
name|expected4
parameter_list|)
block|{
name|assertMapValue
argument_list|(
name|destinationName
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|expected1
block|,
name|expected2
block|,
name|expected3
block|,
name|expected4
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMapValue
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|Object
name|expected1
parameter_list|,
name|Object
name|expected2
parameter_list|,
name|Object
name|expected3
parameter_list|,
name|Object
name|expected4
parameter_list|,
name|Object
name|expected5
parameter_list|)
block|{
name|assertMapValue
argument_list|(
name|destinationName
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|expected1
block|,
name|expected2
block|,
name|expected3
block|,
name|expected4
block|,
name|expected5
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMapValue
parameter_list|(
name|String
name|destinationName
parameter_list|,
name|Object
name|expected1
parameter_list|,
name|Object
name|expected2
parameter_list|,
name|Object
name|expected3
parameter_list|,
name|Object
name|expected4
parameter_list|,
name|Object
name|expected5
parameter_list|,
name|Object
name|expected6
parameter_list|)
block|{
name|assertMapValue
argument_list|(
name|destinationName
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
name|expected1
block|,
name|expected2
block|,
name|expected3
block|,
name|expected4
block|,
name|expected5
block|,
name|expected6
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|protected
name|void
name|assertMapValue
parameter_list|(
name|ActiveMQDestination
name|destination
parameter_list|,
name|Object
name|expected
parameter_list|)
block|{
name|List
name|expectedList
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|expected
operator|==
literal|null
condition|)
block|{
name|expectedList
operator|=
name|Collections
operator|.
name|EMPTY_LIST
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expected
operator|instanceof
name|List
condition|)
block|{
name|expectedList
operator|=
operator|(
name|List
operator|)
name|expected
expr_stmt|;
block|}
else|else
block|{
name|expectedList
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|expectedList
operator|.
name|add
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedList
argument_list|)
expr_stmt|;
name|Set
name|actualSet
init|=
name|map
operator|.
name|get
argument_list|(
name|destination
argument_list|)
decl_stmt|;
name|List
name|actual
init|=
operator|new
name|ArrayList
argument_list|(
name|actualSet
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"map value for destinationName:  "
operator|+
name|destination
argument_list|,
name|expectedList
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ActiveMQDestination
name|createDestination
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

