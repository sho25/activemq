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
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|journal
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
name|org
operator|.
name|jmock
operator|.
name|Expectations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|Mockery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jmock
operator|.
name|lib
operator|.
name|legacy
operator|.
name|ClassImposteriser
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
name|Rule
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
specifier|public
class|class
name|DataFileAccessorPoolTest
block|{
specifier|private
name|Mockery
name|context
decl_stmt|;
annotation|@
name|Rule
specifier|public
name|TemporaryFolder
name|temporaryFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|context
operator|=
operator|new
name|Mockery
argument_list|()
block|{
block|{
name|setImposteriser
parameter_list|(
name|ClassImposteriser
operator|.
name|INSTANCE
parameter_list|)
constructor_decl|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|disposeUnused
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Journal
name|journal
init|=
name|context
operator|.
name|mock
argument_list|(
name|Journal
operator|.
name|class
argument_list|)
decl_stmt|;
name|DataFileAccessorPool
name|underTest
init|=
operator|new
name|DataFileAccessorPool
argument_list|(
name|journal
argument_list|)
decl_stmt|;
name|context
operator|.
name|checking
argument_list|(
operator|new
name|Expectations
argument_list|()
block|{
block|{
name|exactly
argument_list|(
literal|1
argument_list|)
operator|.
name|of
argument_list|(
name|journal
argument_list|)
operator|.
name|getInflightWrites
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|DataFile
name|dataFile
init|=
operator|new
name|DataFile
argument_list|(
operator|new
name|File
argument_list|(
name|temporaryFolder
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"aa"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|underTest
operator|.
name|closeDataFileAccessor
argument_list|(
name|underTest
operator|.
name|openDataFileAccessor
argument_list|(
name|dataFile
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"one in the pool"
argument_list|,
literal|1
argument_list|,
name|underTest
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|disposeUnused
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"0 in the pool"
argument_list|,
literal|0
argument_list|,
name|underTest
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|assertIsSatisfied
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

