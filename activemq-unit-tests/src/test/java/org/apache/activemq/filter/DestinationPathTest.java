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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|test
operator|.
name|TestSupport
import|;
end_import

begin_class
specifier|public
class|class
name|DestinationPathTest
extends|extends
name|TestSupport
block|{
specifier|public
name|void
name|testPathParse
parameter_list|()
block|{
name|assertParse
argument_list|(
literal|"FOO"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"FOO"
block|}
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
literal|"FOO.BAR"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"FOO"
block|,
literal|"BAR"
block|}
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
literal|"FOO.*"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"FOO"
block|,
literal|"*"
block|}
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
literal|"FOO.>"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"FOO"
block|,
literal|">"
block|}
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
literal|"FOO.BAR.XYZ"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"FOO"
block|,
literal|"BAR"
block|,
literal|"XYZ"
block|}
argument_list|)
expr_stmt|;
name|assertParse
argument_list|(
literal|"FOO.BAR."
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"FOO"
block|,
literal|"BAR"
block|,
literal|""
block|}
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertParse
parameter_list|(
name|String
name|subject
parameter_list|,
name|String
index|[]
name|expected
parameter_list|)
block|{
name|String
index|[]
name|path
init|=
name|DestinationPath
operator|.
name|getDestinationPaths
argument_list|(
name|subject
argument_list|)
decl_stmt|;
name|assertArrayEqual
argument_list|(
name|subject
argument_list|,
name|expected
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

