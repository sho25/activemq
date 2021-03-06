begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Licensed to the Apache Software Foundation (ASF) under one or more  *  contributor license agreements.  See the NOTICE file distributed with  *  this work for additional information regarding copyright ownership.  *  The ASF licenses this file to You under the Apache License, Version 2.0  *  (the "License"); you may not use this file except in compliance with  *  the License.  You may obtain a copy of the License at  *  *  http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_class
specifier|public
class|class
name|TransactionsDemo
block|{
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
name|String
name|url
init|=
literal|"tcp://localhost:61616"
decl_stmt|;
name|String
name|user
init|=
literal|null
decl_stmt|;
name|String
name|password
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|1
condition|)
block|{
name|url
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|2
condition|)
block|{
name|user
operator|=
name|args
index|[
literal|1
index|]
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>=
literal|3
condition|)
block|{
name|password
operator|=
name|args
index|[
literal|2
index|]
expr_stmt|;
block|}
name|Retailer
name|r
init|=
operator|new
name|Retailer
argument_list|(
name|url
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|Vendor
name|v
init|=
operator|new
name|Vendor
argument_list|(
name|url
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|Supplier
name|s1
init|=
operator|new
name|Supplier
argument_list|(
literal|"HardDrive"
argument_list|,
literal|"StorageOrderQueue"
argument_list|,
name|url
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|Supplier
name|s2
init|=
operator|new
name|Supplier
argument_list|(
literal|"Monitor"
argument_list|,
literal|"MonitorOrderQueue"
argument_list|,
name|url
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
literal|"Retailer"
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|(
name|v
argument_list|,
literal|"Vendor"
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|(
name|s1
argument_list|,
literal|"Supplier 1"
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
operator|new
name|Thread
argument_list|(
name|s2
argument_list|,
literal|"Supplier 2"
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

