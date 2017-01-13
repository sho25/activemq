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

begin_comment
comment|/**  * @author wcrowell  *  * LargeFile is used to simulate a large file system (e.g. exabytes in size).  * The getTotalSpace() method is intentionally set to exceed the largest  * value of a primitive long which is 9,223,372,036,854,775,807.  A negative  * number will be returned when getTotalSpace() is called.  This class is for  * test purposes only.  Using a mocking framework to mock the behavior of  * java.io.File was a lot of work.  *  */
end_comment

begin_class
specifier|public
class|class
name|LargeFile
extends|extends
name|File
block|{
specifier|public
name|LargeFile
parameter_list|(
name|File
name|parent
parameter_list|,
name|String
name|child
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getTotalSpace
parameter_list|()
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
operator|+
literal|4193L
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getUsableSpace
parameter_list|()
block|{
return|return
name|getTotalSpace
argument_list|()
operator|-
literal|1024L
return|;
block|}
block|}
end_class

end_unit
