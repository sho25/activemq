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
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|StringToListOfActiveMQDestinationConverterTest
block|{
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{     }
annotation|@
name|Test
specifier|public
name|void
name|testConvertToActiveMQDestination
parameter_list|()
block|{
name|List
argument_list|<
name|ActiveMQDestination
argument_list|>
name|result
init|=
name|StringToListOfActiveMQDestinationConverter
operator|.
name|convertToActiveMQDestination
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
name|StringToListOfActiveMQDestinationConverter
operator|.
name|convertToActiveMQDestination
argument_list|(
literal|"[]"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
name|StringToListOfActiveMQDestinationConverter
operator|.
name|convertToActiveMQDestination
argument_list|(
literal|"[  ]"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|result
operator|=
name|StringToListOfActiveMQDestinationConverter
operator|.
name|convertToActiveMQDestination
argument_list|(
literal|"[one,two,three]"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|StringToListOfActiveMQDestinationConverter
operator|.
name|convertToActiveMQDestination
argument_list|(
literal|"[one, two, three  ]"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConvertFromActiveMQDestination
parameter_list|()
block|{
name|String
name|result
init|=
name|StringToListOfActiveMQDestinationConverter
operator|.
name|convertFromActiveMQDestination
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
