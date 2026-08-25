import { mount } from '@vue/test-utils';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import ApplicationsView from '../ApplicationsView.vue';

vi.mock('../../api/applications', () => ({
  listApplications: vi.fn().mockResolvedValue({ data: [] }),
  createApplication: vi.fn().mockResolvedValue({ data: { id: 1, company: 'Acme', position: 'Engineer', stage: 'Applied' } }),
  updateApplication: vi.fn().mockResolvedValue({ data: { id: 1, company: 'Acme', position: 'Engineer', stage: 'Interview' } }),
  archiveApplication: vi.fn().mockResolvedValue({ data: { id: 1, archived: true } }),
  deleteApplication: vi.fn().mockResolvedValue({ data: { id: 1 } })
}));

describe('ApplicationsView', () => {
  let wrapper: any;

  beforeEach(() => {
    wrapper = mount(ApplicationsView);
  });

  it('renders the form and creates an application', async () => {
    expect(wrapper.text()).toContain('Applications');

    await wrapper.get('form').trigger('submit.prevent');
    expect(wrapper.text()).toContain('Acme');
  });

  it('displays overdue follow-up date warning', async () => {
    // Set a past date in the follow-up field
    const pastDate = '2026-01-01'; // Definitely in the past
    await wrapper.get('#followUpDate').setValue(pastDate);

    // Check if the overdue warning is displayed
    expect(wrapper.find('.overdue-warning').exists()).toBe(true);
    expect(wrapper.find('.overdue-warning').text()).toBe('Overdue!');

    // Check if the input has the overdue class
    expect(wrapper.find('#followUpDate').classes()).toContain('overdue');
  });

  it('does not display overdue warning for future dates', async () => {
    // Set a future date in the follow-up field
    const futureDate = '2026-12-31'; // Definitely in the future
    await wrapper.get('#followUpDate').setValue(futureDate);

    // Check that the overdue warning is NOT displayed
    expect(wrapper.find('.overdue-warning').exists()).toBe(false);

    // Check that the input does NOT have the overdue class
    expect(wrapper.find('#followUpDate').classes()).not.toContain('overdue');
  });

  it('does not display overdue warning when no date is set', async () => {
    // Ensure the follow-up date field is empty
    await wrapper.get('#followUpDate').setValue('');

    // Check that the overdue warning is NOT displayed
    expect(wrapper.find('.overdue-warning').exists()).toBe(false);
  });
});