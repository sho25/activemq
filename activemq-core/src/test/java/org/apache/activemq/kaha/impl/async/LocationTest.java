begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|kaha
operator|.
name|impl
operator|.
name|async
package|;
end_package

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
name|Collections
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|Location
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
name|kaha
operator|.
name|impl
operator|.
name|async
operator|.
name|JournalFacade
operator|.
name|RecordLocationFacade
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * Tests the Location Class  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|LocationTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
specifier|transient
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|LocationTest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|synchronized
specifier|public
name|void
name|testRecordLocationImplComparison
parameter_list|()
throws|throws
name|IOException
block|{
name|Location
name|l1
init|=
operator|new
name|Location
argument_list|()
decl_stmt|;
name|l1
operator|.
name|setDataFileId
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|l1
operator|.
name|setOffset
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|Location
name|l2
init|=
operator|new
name|Location
argument_list|(
name|l1
argument_list|)
decl_stmt|;
name|l2
operator|.
name|setOffset
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|Location
name|l3
init|=
operator|new
name|Location
argument_list|(
name|l2
argument_list|)
decl_stmt|;
name|l3
operator|.
name|setDataFileId
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|l3
operator|.
name|setOffset
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|l1
operator|.
name|compareTo
argument_list|(
name|l2
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
comment|// Sort them using a list.  Put them in the wrong order.
name|ArrayList
argument_list|<
name|RecordLocationFacade
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|RecordLocationFacade
argument_list|>
argument_list|()
decl_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|RecordLocationFacade
argument_list|(
name|l2
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|RecordLocationFacade
argument_list|(
name|l3
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
operator|new
name|RecordLocationFacade
argument_list|(
name|l1
argument_list|)
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|l
argument_list|)
expr_stmt|;
comment|// Did they get sorted to the correct order?
name|log
operator|.
name|debug
argument_list|(
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|l
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getLocation
argument_list|()
argument_list|,
name|l1
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|l
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getLocation
argument_list|()
argument_list|,
name|l2
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|l
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getLocation
argument_list|()
argument_list|,
name|l3
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

