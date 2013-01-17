begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|jmx
package|;
end_package

begin_interface
specifier|public
interface|interface
name|PersistenceAdapterViewMBean
block|{
annotation|@
name|MBeanInfo
argument_list|(
literal|"Name of this persistence adapter."
argument_list|)
name|String
name|getName
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Current inflight local transactions."
argument_list|)
name|String
name|getTransactions
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Current data."
argument_list|)
name|String
name|getData
parameter_list|()
function_decl|;
annotation|@
name|MBeanInfo
argument_list|(
literal|"Current size."
argument_list|)
name|long
name|getSize
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

