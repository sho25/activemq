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
name|junit
operator|.
name|Test
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
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|LockFileTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testNoDeleteOnUnlockIfNotLocked
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|,
literal|"lockToTest1"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|lockFile
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
name|lockFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|LockFile
name|underTest
init|=
operator|new
name|LockFile
argument_list|(
name|lockFile
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|underTest
operator|.
name|lock
argument_list|()
expr_stmt|;
comment|// will fail on windows b/c the file is open
if|if
condition|(
name|lockFile
operator|.
name|delete
argument_list|()
condition|)
block|{
name|assertFalse
argument_list|(
literal|"no longer valid"
argument_list|,
name|underTest
operator|.
name|keepAlive
argument_list|()
argument_list|)
expr_stmt|;
comment|// a slave gets in
name|lockFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|underTest
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"file still exists after unlock when not locked"
argument_list|,
name|lockFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeleteOnUnlockIfLocked
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|lockFile
init|=
operator|new
name|File
argument_list|(
name|IOHelper
operator|.
name|getDefaultDataDirectory
argument_list|()
argument_list|,
literal|"lockToTest2"
argument_list|)
decl_stmt|;
name|IOHelper
operator|.
name|mkdirs
argument_list|(
name|lockFile
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
name|lockFile
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|LockFile
name|underTest
init|=
operator|new
name|LockFile
argument_list|(
name|lockFile
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|underTest
operator|.
name|lock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"valid"
argument_list|,
name|underTest
operator|.
name|keepAlive
argument_list|()
argument_list|)
expr_stmt|;
name|underTest
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"file deleted on unlock"
argument_list|,
name|lockFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

