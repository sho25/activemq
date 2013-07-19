begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* * JBoss, Home of Professional Open Source. * Copyright 2010, Red Hat, Inc., and individual contributors * as indicated by the @author tags. See the copyright.txt file in the * distribution for a full listing of individual contributors. * * This is free software; you can redistribute it and/or modify it * under the terms of the GNU Lesser General Public License as * published by the Free Software Foundation; either version 2.1 of * the License, or (at your option) any later version. * * This software is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU * Lesser General Public License for more details. * * You should have received a copy of the GNU Lesser General Public * License along with this software; if not, write to the Free * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA * 02110-1301 USA, or see the FSF site: http://www.fsf.org. */
end_comment

begin_package
package|package
name|org
operator|.
name|hornetq
operator|.
name|javaee
operator|.
name|examples
package|;
end_package

begin_import
import|import
name|org
operator|.
name|hornetq
operator|.
name|javaee
operator|.
name|example
operator|.
name|MDB_CMT_TxRequiredClientExample
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hornetq
operator|.
name|javaee
operator|.
name|example
operator|.
name|server
operator|.
name|MDB_CMT_TxRequiredExample
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|arquillian
operator|.
name|container
operator|.
name|test
operator|.
name|api
operator|.
name|Deployment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|arquillian
operator|.
name|container
operator|.
name|test
operator|.
name|api
operator|.
name|RunAsClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|arquillian
operator|.
name|junit
operator|.
name|Arquillian
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|shrinkwrap
operator|.
name|api
operator|.
name|Archive
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|shrinkwrap
operator|.
name|api
operator|.
name|ShrinkWrap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|shrinkwrap
operator|.
name|api
operator|.
name|spec
operator|.
name|JavaArchive
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
name|runner
operator|.
name|RunWith
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:andy.taylor@jboss.org">Andy Taylor</a>  *         5/21/12  */
end_comment

begin_class
annotation|@
name|RunAsClient
annotation|@
name|RunWith
argument_list|(
name|Arquillian
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MDBCMTSetTXRequiredRunnerTest
block|{
annotation|@
name|Deployment
specifier|public
specifier|static
name|Archive
name|getDeployment
parameter_list|()
block|{
specifier|final
name|JavaArchive
name|ejbJar
init|=
name|ShrinkWrap
operator|.
name|create
argument_list|(
name|JavaArchive
operator|.
name|class
argument_list|,
literal|"mdb.jar"
argument_list|)
decl_stmt|;
name|ejbJar
operator|.
name|addClass
argument_list|(
name|MDB_CMT_TxRequiredExample
operator|.
name|class
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ejbJar
operator|.
name|toString
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ejbJar
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|runExample
parameter_list|()
throws|throws
name|Exception
block|{
name|MDB_CMT_TxRequiredClientExample
operator|.
name|main
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//give the example time to run
name|Thread
operator|.
name|sleep
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

