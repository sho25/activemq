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
name|test
operator|.
name|message
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|test
operator|.
name|JmsTopicSendReceiveWithTwoConnectionsAndEmbeddedBrokerTest
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
name|MapMessage
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

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|NestedMapMessageTest
extends|extends
name|JmsTopicSendReceiveWithTwoConnectionsAndEmbeddedBrokerTest
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NestedMapMessageTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|void
name|assertMessageValid
parameter_list|(
name|int
name|index
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertTrue
argument_list|(
literal|"Should be a MapMessage: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|MapMessage
argument_list|)
expr_stmt|;
name|MapMessage
name|mapMessage
init|=
operator|(
name|MapMessage
operator|)
name|message
decl_stmt|;
name|Object
name|value
init|=
name|mapMessage
operator|.
name|getObject
argument_list|(
literal|"textField"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"textField"
argument_list|,
name|data
index|[
name|index
index|]
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|mapMessage
operator|.
name|getObject
argument_list|(
literal|"mapField"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapField.a"
argument_list|,
literal|"foo"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapField.b"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|23
argument_list|)
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapField.c"
argument_list|,
operator|new
name|Long
argument_list|(
literal|45
argument_list|)
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|=
name|map
operator|.
name|get
argument_list|(
literal|"d"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"mapField.d should be a Map"
argument_list|,
name|value
operator|instanceof
name|Map
argument_list|)
expr_stmt|;
name|map
operator|=
operator|(
name|Map
operator|)
name|value
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mapField.d.x"
argument_list|,
literal|"abc"
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|=
name|map
operator|.
name|get
argument_list|(
literal|"y"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"mapField.d.y is a List"
argument_list|,
name|value
operator|instanceof
name|List
argument_list|)
expr_stmt|;
name|List
name|list
init|=
operator|(
name|List
operator|)
name|value
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"mapField.d.y: "
operator|+
name|list
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"listField.size"
argument_list|,
literal|3
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Found map: "
operator|+
name|map
argument_list|)
expr_stmt|;
name|list
operator|=
operator|(
name|List
operator|)
name|mapMessage
operator|.
name|getObject
argument_list|(
literal|"listField"
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"listField: "
operator|+
name|list
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"listField.size"
argument_list|,
literal|3
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"listField[0]"
argument_list|,
literal|"a"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"listField[1]"
argument_list|,
literal|"b"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"listField[2]"
argument_list|,
literal|"c"
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|int
name|index
parameter_list|)
throws|throws
name|JMSException
block|{
name|MapMessage
name|answer
init|=
name|session
operator|.
name|createMapMessage
argument_list|()
decl_stmt|;
name|answer
operator|.
name|setString
argument_list|(
literal|"textField"
argument_list|,
name|data
index|[
name|index
index|]
argument_list|)
expr_stmt|;
name|Map
name|grandChildMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|grandChildMap
operator|.
name|put
argument_list|(
literal|"x"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|grandChildMap
operator|.
name|put
argument_list|(
literal|"y"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|Map
name|nestedMap
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|nestedMap
operator|.
name|put
argument_list|(
literal|"a"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|nestedMap
operator|.
name|put
argument_list|(
literal|"b"
argument_list|,
operator|new
name|Integer
argument_list|(
literal|23
argument_list|)
argument_list|)
expr_stmt|;
name|nestedMap
operator|.
name|put
argument_list|(
literal|"c"
argument_list|,
operator|new
name|Long
argument_list|(
literal|45
argument_list|)
argument_list|)
expr_stmt|;
name|nestedMap
operator|.
name|put
argument_list|(
literal|"d"
argument_list|,
name|grandChildMap
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setObject
argument_list|(
literal|"mapField"
argument_list|,
name|nestedMap
argument_list|)
expr_stmt|;
name|answer
operator|.
name|setObject
argument_list|(
literal|"listField"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|}
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

